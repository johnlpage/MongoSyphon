package com.johnlpage.mongosyphon;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class RDBMSConnection implements IDataSource {
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
	String paramtext=null;
	Document prevRow=null;
	boolean isMySQL=false;
	
	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#close()
	 */
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
		if(connectionString.contains("mysql://")) {
			isMySQL = true;
			logger.info("Source is MySQL");
		}
		if(usecache)
		{ //Cannot merge AND cache
			cache =  new HashMap<String,Document>();
		}
}

	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#hasResults()
	 */
	public boolean hasResults() {
		
		return (results != null);
	}

	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#Connect(java.lang.String, java.lang.String)
	 */
	public void Connect(String user, String pass) {
		try {
			connection = DriverManager.getConnection(connectionString, user,
					pass);
		} catch (SQLException e) {
			logger.error("Unable to connect to RDBMS");
			logger.error(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		this.user = user;
		this.pass = pass;
	}

	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#RunQuery(java.lang.String, java.util.ArrayList, org.bson.Document)
	 */
	public void RunQuery(String sql, ArrayList<String> params,
			Document parent) {
		try {
			if (stmt == null) {
				stmt = connection.prepareStatement(sql,java.sql.ResultSet.TYPE_FORWARD_ONLY,
			              java.sql.ResultSet.CONCUR_READ_ONLY); // Only create a
															// prepared
															// statement once
				//Only do this bit for MySQL
				//Forces server side cursors
				if(isMySQL) {
					stmt.setFetchSize(Integer.MIN_VALUE);
				}

			}
			// Parameterise every time as we are calling with different values
			int count = 1;
			paramtext=new String();
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
						paramtext = paramtext + "::"+o;
					} else if (o.getClass() == Date.class) {
						stmt.setDate(count, (Date) o);
						paramtext = paramtext + "::"+o;
					} else if (o.getClass() == Integer.class) {
						stmt.setInt(count, (Integer) o);
					} else if (o.getClass() == BigDecimal.class) {
                        stmt.setBigDecimal(count, (BigDecimal)o);
           
						paramtext = paramtext + "::"+o;
					}else if (o.getClass() == Long.class) {
						stmt.setLong(count, (Long) o);
						paramtext = paramtext + "::"+o;
					} else {
						logger.error("Parameter " + p + " is of type " + o.getClass().getName() + " which is not supported");
					}
					count++;
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		//If we have this cached simply return it
		
		//In some JDBC stmttext isnt populated with params
		//stmttext = stmt.toString();
		
		if(cache != null) {
			if(cache.containsKey(paramtext)) {
				results=null;
				metaData=null;
				columnCount=-1;
				incache=true;
				cachedRow = cache.get(paramtext);
				return;//Don't 
			}
		}
		incache=false;
		//logger.info("executing " + stmttext);
		try {

			results = stmt.executeQuery();
	
			metaData = results.getMetaData();
			columnCount = metaData.getColumnCount();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

	}

	
	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#PushBackRow(org.bson.Document)
	 */
	public void PushBackRow(Document row)
	{
		prevRow = row;
	}
	
	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#GetNextRow()
	 */
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
			Object o = results.getObject(i);
			
			if(o instanceof java.sql.Date )
			{
			   java.sql.Date sd = (java.sql.Date)o;
			   
			   o = new java.util.Date(sd.getTime());
			}
			
			if(o instanceof java.sql.Timestamp )
			{
			   java.sql.Timestamp sd = (java.sql.Timestamp)o;
			   o = new java.util.Date(sd.getTime());
			}
			
			row.put(metaData.getColumnLabel(i), o);
		}
		if(cache != null && paramtext != null) {
			cache.put(paramtext, row);
		}
		return row;
	}

	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#getConnectionString()
	 */
	public String getConnectionString() {
		return connectionString;
	}

	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#getUser()
	 */
	public String getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see com.johnlpage.mongosyphon.IDataSource#getPass()
	 */
	public String getPass() {
		return pass;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return "SQL";
	}

}
