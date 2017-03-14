package com.johnlpage.mongosyphon;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.LogManager;

import org.apache.commons.cli.ParseException;
import org.bson.Document;
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
		
		if(options.isHelpOnly()) { System.exit(0);}
		
		if(options.getConfigFile() == null) {
			logger.error("No config file supplied");
			System.exit(1);
		}
		
		try {
			jobdesc =  new JobDescription(options.getConfigFile());
		} catch (FileNotFoundException e) {
			logger.error("Failed to parse config file");
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		if(options.getNewConfigFile() != null)
		{
			ConfigGenerator configGen = new ConfigGenerator(jobdesc);
			configGen.newConfig(options.getNewConfigFile());

			System.exit(1);
			
		}
		//TODO - Add support for pulling all and sending to RDBMS
		MongoConnection target = new MongoConnection(jobdesc);
		Document topParams=new Document();
		Document mongoQuery = jobdesc.getMongoQuery();
		Document mongoOrderBy=jobdesc.getMongoOrderBy();
		Document mongoFields=jobdesc.getMongoFields();
		
		if(mongoOrderBy != null || mongoQuery != null)
		{
			logger.info("Mongo Query is defined ");
			topParams = target.FindOne(mongoQuery, mongoFields, mongoOrderBy);
			if(topParams == null){
				logger.info("Using default");
				topParams = jobdesc.getMongoDefault();
			}
			logger.info("Mongo Query returns" + topParams.toJson());

		} 
		
		long lasttime = System.currentTimeMillis();
		DocumentGenerator generator = new DocumentGenerator(jobdesc,null,topParams);
		Document doc = generator.getNext();
	
		int lastcount=0;
		int currcount=0;
		while(doc != null) {
		
			if(jobdesc.getMode().equals("insert"))
			{
			target.Create(doc);
			} else if(jobdesc.getMode().equals("update")) {
				target.Update(doc, false);
			} else  if(jobdesc.getMode().equals("upsert")) {
			
				target.Update(doc, true);
			} else {
				logger.error("Unknown mode "+jobdesc.getMode());
				System.exit(1);
			}
			currcount++;
			if(currcount - lastcount == 1000) {
				//Change to logging maybe
				long mstime = System.currentTimeMillis()-lasttime;
				System.out.printf("%d records converted in %d seconds at an average of %d records/s\n", currcount,mstime/1000L,
						(1000L*currcount)/mstime);
				
				lastcount=currcount;
			}
			//System.out.println("RESULT" + doc.toJson());
			doc = generator.getNext();
		}
		long mstime = System.currentTimeMillis()-lasttime;
		System.out.printf("%d records converted in %d seconds at an average of %d records/s\n", currcount,mstime/1000L,
				(1000L*currcount)/(mstime+1));
		target.close();
		
		
	}

}
