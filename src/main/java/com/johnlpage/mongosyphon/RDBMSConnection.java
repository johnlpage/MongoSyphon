package com.johnlpage.mongosyphon;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDBMSConnection {
	private String connectionString = null;
	private String user;
	private String pass;
	private Connection connection = null; 
	private Logger logger;
	ResultSet results = null;
	PreparedStatement stmt = null;
	ResultSetMetaData metaData = null;
	int columnCount = 0;
	Map<String,Document> cache=null;
	Boolean incache = false;
	Document cachedRow=null;
	String stmttext=null;
	Document prevRow=null;
	
	public void close() {
		try {
			if (results != null)
				results.close();
			results = null;

		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

	}

	public RDBMSConnection(String connectionString,boolean usecache) {
		logger = LoggerFactory.getLogger(RDBMSConnection.class);
		this.connectionString = connectionString;
		if(usecache)
		{ //Cannot merge AND cache
			cache =  new HashMap<String,Document>();
		}
}

	public boolean hasResults() {
		return (results != null);
	}

	public void Connect(String user, String pass) {
		try {
			connection = DriverManager.getConnection(connectionString, user,
					pass);
		} catch (SQLException e) {
			logger.error("Unable to connect to RDBMS");
			logger.error(e.getMessage());
			System.exit(1);
		}
		this.user = user;
		this.pass = pass;
	}

	public void RunQuery(String sql, ArrayList<String> params,
			Document parent) {
		try {
			if (stmt == null) {
				stmt = connection.prepareStatement(sql,java.sql.ResultSet.TYPE_FORWARD_ONLY,
			              java.sql.ResultSet.CONCUR_READ_ONLY); // Only create a
															// prepared
															// statement once
				stmt.setFetchSize(Integer.MIN_VALUE);

			}
			// Parameterise every time as we are calling with different values
			int count = 1;
			if(params != null)
			{
				for (String p : params) {
					Object o = parent.get(p);
					if(o == null) {
						logger.error(" parameter " + p + " is not defined in parent " + parent.toJson());
						System.exit(1);
					}
					if (o.getClass() == String.class) {
						stmt.setString(count, (String) o);
					} else if (o.getClass() == Date.class) {
						stmt.setDate(count, (Date) o);
					} else if (o.getClass() == Integer.class) {
						stmt.setInt(count, (Integer) o);
		
					}
					count++;
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		//If we have this cached simply return it
		//In theory we should just use a representation of the params
		stmttext = stmt.toString();
		if(cache != null) {
			if(cache.containsKey(stmttext)) {
				results=null;
				metaData=null;
				columnCount=-1;
				incache=true;
				cachedRow = cache.get(stmttext);
				return;//Don't 
			}
		}
		incache=false;
		logger.info("executing " + stmttext);
		try {

			results = stmt.executeQuery();
	
			metaData = results.getMetaData();
			columnCount = metaData.getColumnCount();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

	}

	
	public void PushBackRow(Document row)
	{
		prevRow = row;
	}
	
	public Document GetNextRow() throws SQLException {
		
		//Handle a rewind
		if(prevRow != null) {
			Document r = prevRow;
			prevRow=null;
			return r;
		}
		
		if(incache == true){
			return cachedRow;
		}
		if (results == null) {
			return null;
		}
		if (results.next() == false) {
			return null;
		}

		Document row = new Document();

		for (int i = 1; i <= columnCount; ++i) {
			row.put(metaData.getColumnLabel(i), results.getObject(i));
		}
		if(cache != null && stmttext != null) {
			cache.put(stmttext, row);
		}
		return row;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

}
