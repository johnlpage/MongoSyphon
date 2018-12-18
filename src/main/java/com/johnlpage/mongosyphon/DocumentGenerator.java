package com.johnlpage.mongosyphon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentGenerator {
	private IDataSource connection = null;
	private JobDescription jobdesc;
	Logger logger;
	String sectionName;
	Document section = null;
	Document template = null;
	Document params = null;
	List<?> documentTransformersConfig = null;
	List<IDocumentTransformer> documentTransformers = new ArrayList<IDocumentTransformer>();
	String targetMode = null;
	MongoBulkWriter mongoTarget = null;
	Boolean hasRows = false;
	PrintStream outputStream = null; 
	
	//This is a cache of sub document generators
	HashMap<String, DocumentGenerator>docGens=new HashMap<String,DocumentGenerator>();

	DocumentGenerator(JobDescription jobdesc, String section, Document params,
			Document parentSource) {
		logger = LoggerFactory.getLogger(DocumentGenerator.class);
		this.jobdesc = jobdesc;
		this.sectionName = section;
		this.section = jobdesc.getSection(section);
		if (this.section == null) {
			logger.error("Cannot find section named '" + section
					+ "' in config - aborting");
			System.exit(1);
			;
		}
		this.template = (Document) this.section.get("template", Document.class);
		this.params = params;

		// Does this section define a source, if so we should connect to it
		if (this.section.get("source") == null) {
			if (parentSource == null) {
				logger.error("Cannot find a source defined in section "
						+ section + "or any parent");
				System.exit(1);
			}
			this.section.put("source", parentSource);
		}
		this.documentTransformersConfig = this.section.get("documentTransformers", List.class);
		if (this.documentTransformersConfig != null) {
		    initTransformers();
		}
		connectToSource(this.section.get("source", Document.class));
		connectToTarget(this.section.get("target", Document.class));
	}

	// It's fine to not have a target, most sections won't have one

	private void connectToTarget(Document target) {
		if (target == null) {
			return;
		}
		targetMode = target.getString("mode");
		if (targetMode == null) {
			logger.error("no target mode defined in " + sectionName);
			System.exit(1);
		}
		if (targetMode.equalsIgnoreCase("json")
				|| targetMode.equalsIgnoreCase("xml")) {
			String uri = target.getString("uri");
			if(uri != null  && uri.startsWith("file://"))
			try {
				String fname = uri.substring(7, uri.length());
				logger.info("Writing to "+fname);
				outputStream = new PrintStream(new FileOutputStream(fname));

			} catch (IOException ex) {

				logger.error(ex.getMessage());

			}

			return;
		}
		if (targetMode.equalsIgnoreCase("insert")
				|| targetMode.equalsIgnoreCase("update")
				|| targetMode.equalsIgnoreCase("upsert")
				|| targetMode.equalsIgnoreCase("save")) {
			String targetURI = target.getString("uri");
			if (targetURI == null) {
				logger.error("Target needs a URI in section " + sectionName);
				System.exit(1);
			}
			String namespace = target.getString("namespace");
			if (namespace == null) {
				logger.error("No namespace defined in target section of "
						+ sectionName);
			}

			mongoTarget = new MongoBulkWriter(targetURI, namespace);
		}
	}

	private void connectToSource(Document source) {
		// Do I have a connection - if not get one
		if (connection == null) {

			String connStr = source.getString("uri");
			if (connStr == null) {
				logger.error("No uri deinfed in source for " + sectionName);
				System.exit(1);
			}

			logger.info("connecting to " + connStr);
			if (connStr.startsWith("mongodb:")) {
				connection = new MongoConnection(connStr,
						section.containsKey("cached"));
				connection.Connect(null, null); // In the URI
			} else if (connStr.startsWith("jdbc:")) {
				connection = new RDBMSConnection(connStr,
						section.containsKey("cached"));

				connection.Connect(source.getString("user"),
						source.getString("password"));

			} else {
				logger.error(
						"Don't know how to handle connection uri " + connStr);
				System.exit(1);
			}
		}
	}

	public void setParams(Document params) {
		this.params = params;
	}

	public void close() {
		if (section.containsKey("mergeon") == false) {
			connection.close();
		}
		
		if (outputStream != null ) { outputStream.close();}
	}

	public void runConversion() {

		long lasttime = System.currentTimeMillis();

		Document doc = getNext();

		int lastcount = 0;
		int currcount = 0;
		while (doc != null) {
		    for (IDocumentTransformer t : documentTransformers) {
	            t.transform(doc);
	        }
			if (targetMode.equalsIgnoreCase("subsection")) {

				String subsectionName = section.get("target", Document.class)
						.getString("uri");
				DocumentGenerator subgen;
				if (docGens.containsKey(subsectionName)) {
					subgen = docGens.get(subsectionName);
					subgen.setParams(doc);
				} else {

					subgen = new DocumentGenerator(jobdesc, subsectionName, doc,
							section.get("source", Document.class));
					docGens.put(subsectionName, subgen);
				}
				subgen.runConversion();
			} else if (targetMode.equalsIgnoreCase("JSON")) {
				// TODO: This can be way nicer JSON
				if(outputStream== null){
				System.out.println(doc.toJson());
				} else {
					outputStream.println(doc.toJson());
				}
			} else if (targetMode.equalsIgnoreCase("XML")) {
				// TODO: Add seom XML conversion options
				// As this is poor
				String xml = XML.toString(new JSONObject(doc));
				if(outputStream == null) {
				System.out.println(xml); }
				else {
					outputStream.println(doc.toJson());
				}
			} else {
				if (targetMode.equalsIgnoreCase("insert")) {
					mongoTarget.Create(doc);
				} else if (targetMode.equalsIgnoreCase("update")) {
					mongoTarget.Update(doc, false);
				} else if (targetMode.equalsIgnoreCase("upsert")) {
					mongoTarget.Update(doc, true);
				} else if (targetMode.equalsIgnoreCase("save")) {
                    mongoTarget.Save(doc);
                } else {
					logger.error("Unknown mode " + targetMode);
					logger.error(
							"Should be one of insert,update,upsert,JSON,XML");
					System.exit(1);
				}
			}

			currcount++;
			if (currcount - lastcount == 1000) {
				// Change to logging maybe
				long mstime = System.currentTimeMillis() - lasttime;
				if (targetMode.equalsIgnoreCase("subsection") == false) {
					System.out.printf(
							"%d records converted in %d seconds at an average of %d records/s\n",
							currcount, mstime / 1000L,
							(1000L * currcount) / mstime);
				}
				lastcount = currcount;
			}
			doc = getNext();
		}
		long mstime = System.currentTimeMillis() - lasttime;
		if (targetMode.equalsIgnoreCase("subsection") == false) {
			System.out.printf(
					"%d records converted in %d seconds at an average of %d records/s\n",
					currcount, mstime / 1000L,
					(1000L * currcount) / (mstime + 1));
		}
		if (mongoTarget != null) {
			mongoTarget.close();
		}
	}

	// Return the next document of NULL if we have no more
	public Document getNext() {
		Document rval = null;
		Document row = new Document();

		@SuppressWarnings("unchecked")
		ArrayList<String> paramArray = section.get("params", ArrayList.class);

		if (connection.hasResults() == false) {

			if (connection.getType() == "SQL") {

				connection.RunQuery(
						section.get("query", Document.class).getString("sql"),
						paramArray, params);
			} else if (connection.getType() == "MONGO") {

				Object q = section.get("query");
				if (q instanceof Document) {
					connection.RunQuery(
							((Document) section.get("query")).toJson(),
							paramArray, params);
				} else {
					logger.error(
							"query must be specified as an object for mongodb");
				}
			}
		}

		try {
			row = connection.GetNextRow();

			// If it's null, and we havent fetched ANY rows,
			// and we have a default defined return that
			if (row == null) {
				if (hasRows == false) {
					// null if no default
					row = section.get("query", Document.class).get("default",
							Document.class);
					hasRows = true; // Only once
				}
			} else {
				hasRows = true;
			}
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
			String mergeFields = section.getString("mergeon");
			String bits[] = mergeFields.split("\\=");
			String mergeFrom ;
			String mergeTo = bits[0];
			if(bits.length > 1) {
				 mergeFrom = bits[1];
			} else {
				 mergeFrom = bits[0];
			}
			// Row is the data below
			// Params is the row above
			if (params.containsKey(mergeFrom) && row != null
					&& row.containsKey(mergeTo)) {
				long compval = -1;
				Object parent = params.get(mergeFrom);
				Object child = row.get(mergeTo);
				if (parent.getClass() == child.getClass()) {
					if (parent.getClass() == String.class) {
						String a = (String) parent;
						String b = (String) child;
						compval = a.compareTo(b);
					} else if (parent.getClass() == Integer.class) {
						compval = (Integer) child - (Integer) parent;
					}else if (parent.getClass() == Long.class) {
						compval = (Long) child - (Long) parent;
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
			if (template != null) {

				rval = TemplateRow(template, row);
			} else {
				rval = row;
			}
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
								row, section.get("source", Document.class));
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
					subgen = new DocumentGenerator(jobdesc, v.substring(1), row,
							section.get("source", Document.class));
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
	
	private void initTransformers() {
		for (Object transConfig : documentTransformersConfig) {
			if (transConfig instanceof Document) {
				Document transConfigDoc = (Document) transConfig;
				String className = transConfigDoc.getString("className");
				if (className != null) {
					try {
						Object transformer = Class.forName(className).newInstance();
						if (transformer instanceof IDocumentTransformer) {
							this.documentTransformers.add((IDocumentTransformer) transformer);
						} else {
							logger.warn("documentTransformer not instance of IDocumentTransformer, ignoring");
						}
					} catch (Exception e) {
						logger.error("Error instantiating documentTransformer " + className, e);
						System.exit(1);
					}
				}
			} else {
				logger.warn(
						String.format("Invalid documentTransformers config, expected Document but was %s. Ignoring.",
								transConfig.getClass().getName()));
			}
		}

	}
}
