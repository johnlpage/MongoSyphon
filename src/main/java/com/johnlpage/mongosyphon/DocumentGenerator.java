package com.johnlpage.mongosyphon;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentGenerator {
	private IDataSource connection = null;
	private JobDescription jobdesc;
	Logger logger;
	Document section = null;
	Document template = null;
	Document params = null;
	//This is a cache of sub genetators
	HashMap<String, DocumentGenerator> docGens = new HashMap<String, DocumentGenerator>();

	DocumentGenerator(JobDescription jobdesc, String section, Document params) {
		logger = LoggerFactory.getLogger(DocumentGenerator.class);
		this.jobdesc = jobdesc;
		this.section = jobdesc.getSection(section);
		if (this.section == null) {
			logger.error("Cannot find section named '" + section
					+ "' in config - aborting");
			System.exit(1);
			;
		}
		this.template = (Document) this.section.get("template");
		this.params = params;
	}

	public void setParams(Document params) {
		this.params = params;
	}

	public void close() {
		if (section.containsKey("mergeon") == false) {
			connection.close();
		}
	}

	// Return the next document of NULL if we have no more
	public Document getNext() {
		Document rval = null;

		Document row = new Document();

		// Do I have a connection - if not get one
		if (connection == null) {

			String connStr = jobdesc.getSourceConnection();
			logger.info(connStr);
			if (connStr.startsWith("mongodb:")) {
				connection = new MongoConnection(jobdesc.getSourceConnection(),
						section.containsKey("cached"));
				connection.Connect(jobdesc.getSourceUser(),
						jobdesc.getSourcePassword());
			} else {
				connection = new RDBMSConnection(jobdesc.getSourceConnection(),
						section.containsKey("cached"));

				connection.Connect(jobdesc.getSourceUser(),
						jobdesc.getSourcePassword());

			}

		}

		@SuppressWarnings("unchecked")
		ArrayList<String> paramArray = section.get("params", ArrayList.class);

		if (connection.hasResults() == false) {

			if (connection.getType() == "SQL") {

				connection.RunQuery(section.getString("sql"), paramArray,
						params);
			} else if (connection.getType() == "MONGO") {

				Object q = section.get("mongoquery");
				if (q instanceof Document) {
					connection.RunQuery(
							((Document) section.get("mongoquery")).toJson(),
							paramArray, params);
				} else {
					logger.error("mongoQuery must be specified as an object");
				}
			}
		}

		try {
			row = connection.GetNextRow();
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

		boolean found = false;
		// In MERGE mode, we want to look at each row and see if we want it
		// If it's < the value we want we ignore and take the next one
		// If it's the value we want we use it
		// If it's > the value we want we push it back on the cursor, but
		// tricky in RDBMSConnection

		while (section.containsKey("mergeon") && found == false) {
			String mergeField = section.getString("mergeon");
			// Row is the data below
			// Params is the row above
			if (params.containsKey(mergeField) && row != null
					&& row.containsKey(mergeField)) {
				int compval = -1;
				Object parent = params.get(mergeField);
				Object child = row.get(mergeField);
				if (parent.getClass() == child.getClass()) {
					if (parent.getClass() == String.class) {
						String a = (String) parent;
						String b = (String) child;
						compval = a.compareTo(b);
					} else if (parent.getClass() == Integer.class) {
						compval = (Integer) child - (Integer) parent;
					}
				}
				// Loop again whilst we haven't found one
				if (compval < 0) {
					try {
						row = connection.GetNextRow();
					} catch (Exception e) {
						logger.error(e.getMessage());
						System.exit(1);
					}
					found = false;
				} else if (compval > 0) {
					// Put it back
					connection.PushBackRow(row);
					// Tell parent we ran out of options
					found = false;
					return null;
				} else {
					found = true;
				}
			} else {
				found = true; // Actually not but it gets us out
			}
		}
		// End Merge

		// Apply template to database ROW
		if (row != null) {
			rval = TemplateRow(template, row);
		}
		return rval;
	}

	@SuppressWarnings("unchecked")
	private Document TemplateRow(Document template, Document row) {
		DocumentGenerator subgen;

		Document rval = new Document();
		for (String key : template.keySet()) {
			Object val = template.get(key);
			if (val.getClass() == String.class) {
				String v = (String) val;
				// a $ is a field to be copied from this row
				if (v.startsWith("$")) {
					Object o = row.get(v.substring(1));
					if (o != null)
						rval.append(key, o);
				} else
				// A string with an @ is a lookup of a single value
				if (v.startsWith("@")) {

					if (docGens.containsKey(v.substring(1))) {
						subgen = docGens.get(v.substring(1));
						subgen.setParams(row);
					} else {

						subgen = new DocumentGenerator(jobdesc, v.substring(1),
								row);
						docGens.put(v.substring(1), subgen);
					}
					Document subdoc = subgen.getNext();

					// Two things can happen here - we can add the whole doc
					// OR if there is a _top field we can add that
					if (subdoc != null) {
						if (subdoc.containsKey("_value")) {
							Object o = subdoc.get("_value");
							if (o != null) {
								rval.append(key, o);
							}
						} else {
							if (subdoc.keySet() != null
									&& !subdoc.keySet().isEmpty()) {
								rval.append(key, subdoc);
							}
						}
					}
					subgen.close();
				} else {
					rval.append(key, v); // Literal
				}
			} else if (val.getClass() == ArrayList.class) {
				ArrayList<String> t = (ArrayList<String>) val;
				String v = t.get(0);

				if (docGens.containsKey(v.substring(1))) {
					subgen = docGens.get(v.substring(1));
					subgen.setParams(row);
				} else {
					subgen = new DocumentGenerator(jobdesc, v.substring(1),
							row);
					docGens.put(v.substring(1), subgen);
				}

				ArrayList<Object> subdocs = new ArrayList<Object>();
				Document example = subgen.getNext();
				while (example != null) {
					if (example.containsKey("_value")) {
						Object o = example.get("_value");
						if (o != null) {
							subdocs.add(o);
						}
					} else {
						if (example.keySet() != null
								&& !example.keySet().isEmpty()) {
							subdocs.add(example);
						}
					}
					example = subgen.getNext();
				}
				subgen.close();
				if (subdocs.isEmpty() == false) {
					rval.append(key, subdocs);
				}
			} else if (val.getClass() == Document.class) {
				Document child = TemplateRow((Document) val, row);
				if (child.isEmpty() == false) {
					rval.append(key, child);
				}
			}

		}
		return rval;
	}
}
