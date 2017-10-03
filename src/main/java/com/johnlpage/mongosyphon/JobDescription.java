package com.johnlpage.mongosyphon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.*;

public class JobDescription {
	private Document jobDesc;
	Logger logger;

	JobDescription(String configFile) throws FileNotFoundException {
		logger = LoggerFactory.getLogger(JobDescription.class);
		String config = "";
		try {
			config = new String(Files.readAllBytes(Paths.get(configFile)),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error(e.getMessage());

			System.exit(1);
		}
		// Better errors from this parser
		try {
			@SuppressWarnings("unused")
			JSONObject obj = new JSONObject(config);
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		jobDesc = Document.parse(config);

	}

	public Map<String, Object> getJobDesc() {
		return jobDesc;
	}

	public String getSourceConnection() {
		return jobDesc.getString("sourceConnection");
	}

	public String getMode() {
		String rval = null;
		if (jobDesc.containsKey("outputMode")) {
			rval = jobDesc.getString("outputMode");
		}
		return rval;
	}

	public String getSourceUser() {
		return jobDesc.getString("sourceUser");
	}

	public String getSourcePassword() {
		return jobDesc.getString("sourcePassword");
	}

	public String getDestinationMongoURI() {
		return jobDesc.getString("mongoDestConnection");
	}

	public String getDestinationMongoDatabase()

	{
		return jobDesc.getString("mongoDestDatabase");

	}

	public Document getDestinationMongoDefault() {
		return (Document) jobDesc.get("mongoDefault");

	}

	public String getDestinationMongoCollection() {
		return jobDesc.getString("mongoDestCollection");
	}

//TODO get rid of this special case
	public Document getDestiantionMongoQuery() {
		return (Document) jobDesc.get("mongoQuery");
	}

	public Document getDestinationMongoFields() {
		return (Document) jobDesc.get("mongoFields");
	}

	public Document getDestinationMongoOrderBy() {
		return (Document) jobDesc.get("mongoOrderBy");
	}

	public Document getSection(String heading) {
		if (heading == null) {
			if (jobDesc.getString("startAt") == null) {
				logger.error("No startAt defined");
				System.exit(1);
				;
			}
			return jobDesc.get(jobDesc.getString("startAt"), Document.class);
		}
		return jobDesc.get(heading, Document.class);
	}

}
