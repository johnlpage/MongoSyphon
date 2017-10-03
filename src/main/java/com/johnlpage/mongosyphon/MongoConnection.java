package com.johnlpage.mongosyphon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoConnection implements IDataSource {
	private String connectionString = null;
	private String user;
	private String pass;
	private Logger logger;
	MongoClient mongoClient = null;
	MongoDatabase db = null;
	MongoCollection<Document> collection = null;
	int columnCount = 0;
	MongoCursor<Document> results = null;
	Map<String, Document> cache = null;
	Boolean incache = false;
	Document cachedRow = null;
	String stmttext = null;
	Document prevRow = null;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#close()
	 */
	public void close() {
		try {
			if (results != null)
				results.close();
			results = null;

		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

	}

	public MongoConnection(String connectionString, boolean usecache) {
		logger = LoggerFactory.getLogger(MongoConnection.class);
		this.connectionString = connectionString;
		if (usecache) { // Cannot merge AND cache
			cache = new HashMap<String, Document>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#hasResults()
	 */
	public boolean hasResults() {
		return (results != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#Connect(java.lang.String,
	 * java.lang.String)
	 */
	public void Connect(String user, String pass) {
		try {
			logger.info("Connecting to " + connectionString);

			// Authaenticate
			// MongoCredential credential =
			// MongoCredential.createCredential(user,
			// "admin",
			// pass); //Only users on admin as that will be mandatory in 3.6

			mongoClient = new MongoClient(new MongoClientURI(connectionString));

			mongoClient.getDatabase("admin")
					.runCommand(new Document("ping", 1));

		} catch (Exception e) {
			logger.error("Unable to connect to MongoDB");
			logger.error(e.getMessage());
			System.exit(1);
		}
		this.user = user;
		this.pass = pass;
	}

	private Object getFieldFromDocument(Document o, String fieldName) {

		final String[] fieldParts = fieldName.split("\\.");

		int i = 1;
		Object val = o.get(fieldParts[0]);

		while (i < fieldParts.length && val instanceof Document) {
			val = ((Document) val).get(fieldParts[i]);
			i++;
		}

		return val;
	}

	// Inject numbered parameters
	@SuppressWarnings("unchecked")
	private Document ParameteriseDocument(Document d, ArrayList<String> params,
			Document parent) {
		Document rval = new Document();
		for (String key : d.keySet()) {
			Object value = d.get(key);
			// A string may be parameterised
			if (value instanceof String) {
				if (((String) value).startsWith("$")) {
					try {
						// Only $1 to $9 allowed
						if (Character.isDigit(((String) value).charAt(1))) {
							String paramNoString = ((String) value).substring(1,
									2);
							int paramNo = Integer.parseInt(paramNoString);
							value = getFieldFromDocument(parent,
									params.get(paramNo - 1)); // Replace with
																// parent obj
						}
					} catch (Exception e) {
						logger.info(e.getMessage());
					}
				}
			} else if (value instanceof Document) {
				// Recurse down
				value = ParameteriseDocument((Document) value, params, parent);
			} else if (value instanceof ArrayList) {
				ArrayList<Object> newList = new ArrayList<Object>();
				for (Object lv : (ArrayList<Object>) value) {

					if (lv instanceof Document) {
						newList.add(ParameteriseDocument((Document) lv, params,
								parent));
					} else {
						// It's a scalar but we still need it
						Document tmpDoc = new Document("value", "lv");
						newList.add(ParameteriseDocument(tmpDoc, params, parent)
								.get("value"));
					}
				}
				value = newList;
			}
			rval.append(key, value);
		}
		return rval;
	}

	@SuppressWarnings("unchecked")
	public void RunQuery(String mongoql, ArrayList<String> params,
			Document parent) {
		Document parameterised = new Document();
		Document mongoQLDoc = new Document();
		Document find = null;
		ArrayList<Document> aggregate = null;
		Document projection = null;
		Document sort = null;
		Integer limit = new Integer(0);
		try {
			mongoQLDoc = Document.parse(mongoql);
			find = mongoQLDoc.get("find", Document.class);
			try {
				aggregate = mongoQLDoc.get("aggregate", ArrayList.class);
			} catch (Exception e) {
				logger.error("Aggregation pipeline not specified correctly as an Array");
				logger.error(e.getMessage());
				System.exit(1);
			}
			if (find == null && aggregate == null) {
				// Get all
				find = new Document();
			}

			projection = mongoQLDoc.get("project", Document.class);
			sort = mongoQLDoc.get("sort", Document.class);
			limit = mongoQLDoc.get("limit", Integer.class);

			if (aggregate != null) {
				if (find != null || sort != null || projection != null || limit != null) {
					logger.error(
							"You cannot specify find, sort or project with aggreagte");
					System.exit(1);
				}
			}

			// The only thing we are letting them parameterise is a Match at the
			// start
			if (aggregate != null) {
				Document firstMatch = aggregate.get(0);
				if (firstMatch != null) {
					Document matchDoc = firstMatch.get("$match",
							Document.class);
					if (matchDoc == null) {
						logger.error(
								"An aggregation must start with a $match");
						System.exit(1);
					}
					parameterised = ParameteriseDocument(matchDoc, params,
							parent);
					firstMatch.put("$match", parameterised);
				} else {
					logger.error(
							"You cannot specify an empty aggregation pipeline");
					System.exit(1);
				}
			} else {
				// Parameterise the find
				parameterised = ParameteriseDocument(find, params, parent);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

		// If we have this cached simply return it
		// In theory we should just use a representation of the params
		stmttext = parameterised.toString();
		if (cache != null) {
			if (cache.containsKey(stmttext)) {
				results = null;
				incache = true;
				cachedRow = cache.get(stmttext);
				return;// Don't
			}
		}
		incache = false;
		logger.info("executing " + stmttext);

		String databaseName = mongoQLDoc.getString("database");
		String collectionName = mongoQLDoc.getString("collection");
		MongoCollection<Document> collection = mongoClient
				.getDatabase(databaseName).getCollection(collectionName);

		if (find != null) {
			FindIterable<Document> fi = collection.find(parameterised);
			if (projection != null) {
				fi = fi.projection(projection);
			}
			if (sort != null) {
				fi = fi.sort(sort);
			}
			if (limit != null) {
				fi = fi.limit(limit);
			}
			results = fi.iterator();
		} else {
			// Aggregation
			results = collection.aggregate(aggregate).iterator();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#PushBackRow(org.bson.Document)
	 */
	public void PushBackRow(Document row) {
		prevRow = row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#GetNextRow()
	 */
	public Document GetNextRow() throws Exception {

		// Handle a rewind
		if (prevRow != null) {
			Document r = prevRow;
			prevRow = null;
			return r;
		}

		if (incache == true) {
			return cachedRow;
		}
		if (results == null) {
			return null;
		}
		if (results.hasNext() == false) {
			
			return null;
		}

		Document row = results.next();
		
		if (cache != null && stmttext != null) {
			cache.put(stmttext, row);
		}
		return row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#getConnectionString()
	 */
	public String getConnectionString() {
		return connectionString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#getUser()
	 */
	public String getUser() {
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.johnlpage.mongosyphon.IDataSource#getPass()
	 */
	public String getPass() {
		return pass;
	}

	public String getType() {
		return "MONGO";
	}

}
