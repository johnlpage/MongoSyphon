package com.johnlpage.mongosyphon;

import java.io.FileNotFoundException;
import java.util.logging.LogManager;

import org.apache.commons.cli.ParseException;
import org.bson.Document;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO
//Add update/upsert mode
//Documentation

public class MongoSyphon {
	private static final String version = "0.1.1";
	private static CommandLineOptions options;
	private static JobDescription jobdesc;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		Logger logger = LoggerFactory.getLogger(MongoSyphon.class);
		logger.info("MongoSyphon Version " + version);

		try {
			options = new CommandLineOptions(args);
		} catch (ParseException e) {
			logger.error("Failed to parse command line options");
			logger.error(e.getMessage());
			System.exit(1);
		}

		if (options.isHelpOnly()) {
			System.exit(0);
		}

		if (options.getConfigFile() == null) {
			logger.error("No config file supplied");
			System.exit(1);
		}

		try {
			jobdesc = new JobDescription(options.getConfigFile());
		} catch (FileNotFoundException e) {
			logger.error("Failed to parse config file");
			logger.error(e.getMessage());
			System.exit(1);
		}

		//TODO - Revise or remove this
		if (options.getNewConfigFile() != null) {
			ConfigGenerator configGen = new ConfigGenerator(jobdesc);
			configGen.newConfig(options.getNewConfigFile());

			System.exit(1);

		}
		// TODO - Add support for pulling all and sending to RDBMS
		// TODO - Remove the need for a target if XML or JSON, add an outfile
		MongoBulkWriter target = null;
		String mode = jobdesc.getMode();
		if(mode == null) {
				logger.error("outputMode must be one of insert,update,upsert,JSON,XML");
				System.exit(1);
		}
		
		//No writed needed for the text modes
		if ( mode.equalsIgnoreCase("JSON") == false
				&& mode.equalsIgnoreCase("XML") == false) {
			target = new MongoBulkWriter(jobdesc);
		}


		long lasttime = System.currentTimeMillis();
		DocumentGenerator generator = new DocumentGenerator(jobdesc, null,
				null);
		Document doc = generator.getNext();

		int lastcount = 0;
		int currcount = 0;
		while (doc != null) {
		
			
			if (target != null) {
				if (mode.equalsIgnoreCase("insert")) {
					target.Create(doc);
				} else if (mode.equalsIgnoreCase("update")) {
					target.Update(doc, false);
				} else if (mode.equalsIgnoreCase("upsert")) {

					target.Update(doc, true);
				}
			}
			// TODO - Output to a named file
			else if (jobdesc.getMode().equalsIgnoreCase("JSON")) {
				// This can be way nicer JSON
				System.out.println(doc.toJson());
			} else if (jobdesc.getMode().equalsIgnoreCase("XML")) {
				// System.out.println(doc.toJson());
				// Need some though about this - this conversion is totally
				// wrong for arrays
				// Need a better conversion - possibly manual
				// Attributes could be handles using the __text method or the
				// $attr method
				String xml = XML.toString(new JSONObject(doc));
				System.out.println(xml);
			} else {
				logger.error("Unknown mode " + jobdesc.getMode());
				logger.error("Should be one of insert,update,upsert,JSON,XML");
				System.exit(1);
			}
			currcount++;
			if (currcount - lastcount == 1000) {
				// Change to logging maybe
				long mstime = System.currentTimeMillis() - lasttime;
				System.out.printf(
						"%d records converted in %d seconds at an average of %d records/s\n",
						currcount, mstime / 1000L,
						(1000L * currcount) / mstime);

				lastcount = currcount;
			}
			// System.out.println("RESULT" + doc.toJson());
			doc = generator.getNext();
		}
		long mstime = System.currentTimeMillis() - lasttime;
		System.out.printf(
				"%d records converted in %d seconds at an average of %d records/s\n",
				currcount, mstime / 1000L, (1000L * currcount) / (mstime + 1));
		if(target != null) {
			target.close();
		}

	}

}
