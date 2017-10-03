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



		
		DocumentGenerator generator = new DocumentGenerator(jobdesc, null,
				null,null);
		//Top level generator we just tell to get on with it
		generator.runConversion();

	}

}
