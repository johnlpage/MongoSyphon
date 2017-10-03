package com.johnlpage.mongosyphon;

import java.util.ArrayList;

import org.bson.Document;

public interface IDataSource {

	void close();

	boolean hasResults();

	void Connect(String user, String pass);

	void RunQuery(String sql, ArrayList<String> params, Document parent);

	void PushBackRow(Document row);

	Document GetNextRow() throws Exception;

	String getConnectionString();

	String getUser();

	String getPass();

	String getType();

}