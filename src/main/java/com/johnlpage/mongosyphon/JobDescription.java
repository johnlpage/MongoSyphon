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
	JobDescription(String configFile) throws FileNotFoundException
	{
		logger = LoggerFactory.getLogger(JobDescription.class);
	    String config = "";
		try {
			config = new String(Files.readAllBytes(Paths.get(configFile)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error(e.getMessage());
			
		
			
			System.exit(1);
		}
		//Better errors from this parser
		try{
			JSONObject obj = new JSONObject(config);
		} catch(Exception e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
	    jobDesc = Document.parse(config);
	    
	}

	public Map<String, Object> getJobDesc() {
		return jobDesc;
	}
	
	public String getDatabaseConnection()
	{
		return jobDesc.getString("databaseConnection");
	}
	
	public String getMode()
	{
		String rval = "insert";
		if(jobDesc.containsKey("mode"))
		{
		rval =  jobDesc.getString("mode");
		}
		return rval;
	}
	
	public String getDatabaseUser()
	{
		return jobDesc.getString("databaseUser");
	}
	
	public String getDatabasePass()
	{
		return jobDesc.getString("databasePassword");
	}
	
	public String getMongoURI()
	{
		return jobDesc.getString("mongoConnection");
	}

	public String getMongoDatabase()
	{
		return jobDesc.getString("mongoDatabase");
		
	}
	
	public Document getMongoDefault()
	{
		return (Document)jobDesc.get("mongoDefault");
		
	}
	
	
	public String getMongoCollection()
	{
		return jobDesc.getString("mongoCollection");
	}
	
	public Document getMongoQuery()
	{
		return (Document)jobDesc.get("mongoQuery");
	}
	
	public Document getMongoFields()
	{
		return (Document)jobDesc.get("mongoFields");
	}
	
	public Document getMongoOrderBy()
	{
		return (Document)jobDesc.get("mongoOrderBy");
	}
	
	
	public Document getSection(String heading)
	{
		if (heading == null) {
			if(jobDesc.getString("startAt")==null) {
				logger.error("No startAt defined");
				System.exit(1);;
			}
			return jobDesc.get(jobDesc.getString("startAt"), Document.class);
		}
		return jobDesc.get(heading, Document.class);
	}
	
}
