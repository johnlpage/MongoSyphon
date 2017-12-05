package com.johnlpage.mongosyphon;

import org.bson.Document;

public interface IDocumentTransformer {

	public void transform(Document source);

}
