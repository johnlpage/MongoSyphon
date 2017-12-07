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
		
		// Handle \ followed by newline as line continuation
		config = config.replaceAll("\\\\\n", "");
		
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

	public Document getSection(String heading) {
		if (heading == null) {
			return jobDesc.get("start", Document.class);
		}
		return jobDesc.get(heading, Document.class);
	}

}
