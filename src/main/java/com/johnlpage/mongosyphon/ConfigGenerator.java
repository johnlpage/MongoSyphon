package com.johnlpage.mongosyphon;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



public class ConfigGenerator {
	JobDescription jobdesc = null;
	private Logger logger;

	public ConfigGenerator(JobDescription jobdesc) {
		this.jobdesc = jobdesc;
		logger = LoggerFactory.getLogger(ConfigGenerator.class);
	}

	public void newConfig(String fileName) {
		Connection connection = null;
		Document configData = new Document();

		try {
			connection = DriverManager.getConnection(
					jobdesc.getDatabaseConnection(), jobdesc.getDatabaseUser(),
					jobdesc.getDatabasePass());

			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				String catalog = rs.getString(1);
				String schema = rs.getString(2);
				String table = rs.getString(3);
				Document template = new Document();
				ResultSet columns = md.getColumns(catalog, schema, table, "%");
				while (columns.next()) {
					String columnName = columns.getString("COLUMN_NAME");
					template.append(columnName, "$" + columnName);
				}
				Map<String, Object> section = new HashMap<String, Object>();
				section.put("sql", "SELECT * FROM " + table + " ");
			
				section.put("template", template);

				configData.put(table + "section", section);
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}


		DumperOptions options = new DumperOptions();
		options.setAllowReadOnlyProperties(true);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);

		
		String configjs = configData.toJson();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(configjs);
		
		String json = gson.toJson(je);
		
		
		StringBuilder newJson = new StringBuilder();
		int arrays = 0;
		
		// Collapse inner newlines
		boolean inquotes = false;
		for(int c=0;c<json.length();c++)
		{
			char inChar = json.charAt(c);
			if(inChar == '[') { arrays++; }
			if(inChar == ']') { arrays--; }
			if(inChar == '"') {inquotes = !inquotes; }
		
			if(arrays >1 && inChar == '\n')
			{
				continue;
			}
			if(arrays > 1 && !inquotes && inChar == ' ' ) { continue;}
			newJson.append(json.charAt(c));
		}
		
		try {
			PrintWriter out = new PrintWriter(fileName);
			out.println(newJson.toString());
			out.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
	
	}

}
