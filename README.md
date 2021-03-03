# MongoSyphon

N.B MongoSyphon version 2 has had a considerable overhaul of it's configuration file format and functionality.


## Introduction

MongoSyphon is an Extract/Transform/Load (ETL) Engine designed specifically to Merge and Transform data into Document forms. It can read from RDBMS tables or MongoDB and output to JSON, XML or directly into MongoDB. It performs time and memory efficient joins of data internally where the underlying database does no support it or the merge of data is between multiple sources.

When sending data to MongoDB it can make use of the powerful native document update facilities. This differs from many other ETL tools which target relational structures and have a minimal MongoDB add-on. MongoSyphon can be used both for an initial bulk transfer and for ongoing updates.

MongoSyphon does not contain an explicit Change Data Capture (CDC) capability but is able to perform basic CDC tasks through SQL and MongoDB querying or make use of change tables generated by an external CDC.

It required that users are competent in SQL and know the structure of the source data. It is also assumed they can make a judgement and/or measure the impact on the source system when running MongoSyphon.

It is also assumed that the user is able to understand and define the MongoDB target collection.

A core tenet of MongoSyphon is to push work to either the source or to MongoDB wherever possible. The engine itself is intended to me small, lightweight and fast without huge memory requirements.

There is no GUI for MongoSyphon, job scheduling should be via cron or similar for ongoing processes.


## Short Demo
### Build
1. Checkout from Github
2. mvn package

### Prerequisites

1. MongoDB on port 27017 with auth to write (root/password)
2. MySQl on port 3306 with auth to write (root/password)
3. mongo client and mysql in your path

### Demo

First make a sample database in your RDBMS (MySQL)

```
> cd example
> cat mkpets.sql

drop database if exists sdemo;
create database sdemo;
use sdemo;

create table owner ( ownerid int primary key, name VARCHAR(20), address VARCHAR(60));
CREATE TABLE species ( speciesid int primary key ,  species VARCHAR(20));
CREATE TABLE pet ( petid int primary key, name VARCHAR(20), owner int, species int);

insert into species values(1,"Dog");
insert into species values(2,"Cat");

insert into owner values (1,"John","22 Accacia Avenue");
insert into owner values (2,"Sarah","19 Main Street");

insert into pet values (1,"Brea",1,1);
insert into pet values (2,"Harvest",1,1);
insert into pet values (3,"Mittens",2,2);


> mysql -uroot -ppassword < mkpets.sql
> mysql -u root -ppassword
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 595
Server version: 5.7.17 MySQL Community Server (GPL)

Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> describe sdemo;
ERROR 1046 (3D000): No database selected
mysql> use sdemo;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+-----------------+
| Tables_in_sdemo |
+-----------------+
| owner           |
| pet             |
| species         |
+-----------------+
3 rows in set (0.00 sec)

mysql> exit
```

Now we have a three table schema each _owner_ has 0 or more pets, each _pet_ has exactly one _species_ from a small list.

```
>cat owners.js
{
	start: {
		source: {
			uri:  "jdbc:mysql://localhost:3306/sdemo?useSSL=false",
			user: "root",
			password: "password"
		},
		target: {
			mode: "insert",
			uri: "mongodb://localhost:27017/",
			namespace: "sdemo.owners"
		},
		template: {
			_id: "$ownerid",
			name: "$name",
			address : "$address",
			pets : [ "@petsection" ]
		},
		query:{
		   sql: 'SELECT * FROM owner'
		}
	},

	petsection: {
		template: {
			petid: "$petid",
			name: "$name",
			species : "@speciessection"
		},
		query:{
			sql: 'SELECT * FROM pet where owner = ?'
		},
		params: [ "ownerid" ]
	},

	speciessection: {
		template: {
			_value : "$species"
		},
		query: {
			sql: 'SELECT * from species where speciesid = ?'
		},
		params : [ "species" ]
	}
}
>java -jar ../bin/MongoSyphon.jar -c owners.js 
2 records converted in 0 seconds at an average of 7 records/s
>mongo -uroot -ppassword
> use sdemo
switched to db sdemo
> db.owners.find().pretty()
{
	"_id" : 1,
	"name" : "John",
	"address" : "22 Accacia Avenue",
	"pets" : [
		{
			"petid" : 1,
			"name" : "Brea",
			"species" : "Dog"
		},
		{
			"petid" : 2,
			"name" : "Harvest",
			"species" : "Dog"
		}
	]
}
{
	"_id" : 2,
	"name" : "Sarah",
	"address" : "19 Main Street",
	"pets" : [
		{
			"petid" : 3,
			"name" : "Mittens",
			"species" : "Cat"
		}
	]
}
>exit
```



## WARNING Version 2.0.1

**MongoSyphon is an earluy stage, open-source tool and has the following characteristics**

* Not well enough tested
* Limited Error Handling
* At-will support from the Author

These will improve over time but usage is **at your own risk** and there are no implied warranties or promises it won't eat your data or lie to you.

Please report any bugs of issues you find.

This is released with an Apache 2.0 license.

## Command line
```
Usage: java -jar MongoSyphon.jar [args]

Args:
   -c <config file>
   -h <help>
   -n <new output config>
```

## Config Files

### Nature

MongoSyphon is driven from JSON format configuration files. Early prototypes used YAML configuration files however this resulted in embedding JSON in YAML and was ultimately less readable. This also lends itself to storing the ETL configurations inside MongoDB itself if desired.

Each configuraton file defines a complete ETL process. At this time that is either an insert, update or upsert into a single MongoDB collection with documents generated from one or more relational tables or from a single MongoDB instance/cluster.

In future parallel generation of codepentant records into multiple collections to generate more 'relational' data in MongoDB may also be supported.


### Config Format

####  Sections and the start point

The config file is divided into named _sections_. Every config file must have a section called _start_  which is the first section processes. A section defines a SQL Query, Mongo Query or Aggregation, a Template, a Parameters section if required and any cacheing/execution options.

A section can have a source, target, query and template.

A simple section might look like this

```
	

	start: {
		source : {
			uri: "jdbc:mysql://localhost:3306/jmdb?useSSL=false",
			user: "toor",
			password: "p4ssword"
		},
		target : {
			mode: 'insert',
			uri: 'mongodb://localhost:27017',
			namespace: "test.example"
		},
		template: {
			_id: "$ownerid",
			name: "$name",
			address : "$address",
		},
		query: {
			sql: 'SELECT * FROM owner'
		}
	}
```


The simple description of the logic here is.

* Run the  Query
* For Each Row/Document in Results:
	- Apply the Row values to the template to make a Document.
	- Sent that result to the targer.
	- `$name` means the contents of a column called name.
	- without the $ the explicit value is included.
	- Null or empty columns are not include in the output.
	- 
#### Source Details

Each section must have a source, a place from which to pull data  - is a source is not explicitly defined for a section then it is inherited from the parent section calling it. Connection to an RDBMS is via JDBC and a suitable JDBC Driver must be in your classpath. JDBC drivers for MySQL and Prostgres are included in the JAR and the Maven build file.

The source part of a section has the following members.

Option|Description|Mandatory|
|------|-----------|---------|
| uri| A JDBC or MongoDB Connection string to the database of your choice, you must have an appropriate driver jar in your classpath for an RDBMS, mysql and postgres build with MongoSyphon and are in it's JAR|Yes|
|user| For JDBC only - user credentials, in MongoDB they are in the URI| Yes |
|password|For JDBC only - user credentials, in MongoDB they are in the URI| Yes |



The full range of Connection string options are available for MongoDB and the RDBMS. To use username/password authentication for MongoDB it should be included in the URI [Mongo URI Format](https://docs.mongodb.com/manual/reference/connection-string/). In future additional password options may be included to avoid passwords in configuration files when using passworded login.

#### Target Details

The target specifies either a MongoDB URI and namespace to write to or an output file format. It also specified, in the MOngoDB cae whther to insert, update or upsert data

|Option|Description|Mandatory|
|------|-----------|---------|
|mode| one of insert,upsert,update,save,JSON, XML or subsection|No|
| uri| A MongoDB Connection string to the database of your choice, you must have an appropriate driver jar in your classpath for an RDBMS, mysql and postgres build with MongoSyphon and are in it's JAR|Yes|
user| A user who can log in and read from the RDBMS| Yes |
|password|A password for the user above , for MongoDB put it in the connection string instead| Yes |


#### Nested Sections

The power of MongSyphon is in it's ability to build nested document structures - to do this us uses the concept of nested sections. When evaluating a section, MongoSyphon can evaluate the contents of another section which may return one or more documented and embed them in the higher level section. This is specified using the `@section` notation.

Taking our Pet Owners example.

```
	

	start: {
		source : { ... },
		target: { ... },
		template: {
			_id: "$ownerid",
			name: "$name",
			address : "$address",
			pets : [ "@petsection" ]
		},
		query : {
		  sql: 'SELECT * FROM owner'
		}
	},


	petsection: {
		template: {
			petid: "$petid",
			name: "$name",
		},
		query : { sql: 'SELECT * FROM pet where owner = ?' } , 
		params: [ "ownerid" ]

	}
```

In `ownerssection` we specify that for each owner we want an Array (denoted by the square brackets) of results of `petsection`. If the array is empty the field will be omitteed rather than embed an empty array.

`petsection` is similar to `ownersection` except the SQL uses a WHERE clause with SQL placehoders _?_ in the query to retrieve a subset of the data. In this case oly pets for a given owner. The values used to parameterise the query are defined in the `params:` value which is always an array of column names from the parent or calling section. In this case ownerid from ownersection.

***It is Critical that these queries are correctly indexed on the RDBMS***

#### Single Embedded Objects

Calling a sub section with the parameter surrounded by square brackets embeds an array in the parent. There are also two ways to create a nested object rather than array.

The simplest is used to take values from the current query and present them nested. Imagine we had an RDBMS Row of the form

|Name|StreetNumber|StreetName|Town|Country|PostalCode|
|----|------------|----------|----|-------|----------|
|Charlotte|22|Accacia Avenue|Staines|UK|ST1 8BP|

We could have a template like this.

```
 peoplesection: {
        template: {
            name: "$name",
            streetnumber: "$streetnumber",
            streetname: "$streetname",
            town: "$town",
            country: "$country",
            postalcode: "$postalcode"
        },
        query: { sql: 'SELECT * FROM people' }
    }
```

Or we may want a more nested schema from that single row - like this


```
 peoplesection: {
        template: {
	            name: "$name",
	            address : {
		            streetnumber: "$streetnumber",
		            streetname: "$streetname",
		            town: "$town",
		            country: "$country",
		            postalcode: "$postalcode"
            }
        },
        query: { sql: 'SELECT * FROM people' }
    }
```

Which is also valid.


We can also embed an object from a sub-section by calling the subsection but _not including the square brackets_ this causes the subsection to be evaluated _only for the first matching value_. In our pets example we may do this for a lookup table like the pet's species like so. This should only be used normally where you *know* there is only a single matching value.


```

	petsection: {
		template: {
			petid: "$petid",
			name: "$name",
			type : "@speciessection"
		},
		query : { sql: 'SELECT * FROM pet where owner = ?' },
		params: [ "ownerid" ]

	},

	speciessection: {
		template: {
			species : "$species",
			breed : "$breed"
		},
		query : { sql: 'SELECT * from species where speciesid = ?' },
		params : [ "species" ]

	}
```
	
This would result in a document like:

```
{
	"_id" : 2,
	"name" : "Sarah",
	"address" : "19 Main Street",
	"pets" : [
		{
			"petid" : 3,
			"name" : "Mittens",
			"type" : { species:  "Cat",
			           breed : "Siamese"
			         }
		}
	]
}
```

#### Scalar Values

Sometimes you have only a single value you are interested in being returned from a sub-section. This can be the case regardless of whther it is being returned to an array or a scalar at a higher level - you don't want to return an object as it will have a single member.

Mongosyphon lets us use the special value `_value` to denote that the output of this section should not be an object but in fact a scalar.

This is often the case when the section relates to a lookup table. If, in our example above, we did not have _species_ *and* _breed_ for each pet but just the species then we may choose to define it differently. By putting the species values into `_value` we can get a different output.



```

	petsection: {
		template: {
			petid: "$petid",
			name: "$name",
			type : "@speciessection"
		},
		query: { sql: 'SELECT * FROM pet where owner = ?'},
		params: [ "ownerid" ]

	},

	speciessection: {
		template: {
			_value : "$species",
		},
		query: { sql: 'SELECT * from species where speciesid = ?' },
		params : [ "species" ]

	}
```

We get the output


```
{
	"_id" : 2,
	"name" : "Sarah",
	"address" : "19 Main Street",
	"pets" : [
		{
			"petid" : 3,
			"name" : "Mittens",
			"type" : "Cat"
		}
	]
}
```

Note that _type_ is not a String value not an object.

### Caching Sections

The default mode in mongosyphon is to perform standalone queries (using pre-prepared statements and pooled connections for efficiency) to retrieve each lower section. In some cases this can result in the same query being performed many times - for example retrieving the "species = cat" data in the above example for each cat in the database.

Most RDBMS will cache frequent queries like this but there is still considerable overhead in round trips to the server, preparing the query etc. Mongosyphon allows you to cache a section at the client side if it is expected to return a relatively small number of discrete results. For example we know there are only a few pet species so we can add the parameter `cached: true` in the section. Using this will cause MongoSyphon to cache the output for each set of input params and greatly reduce calls to the server.

Be aware that you can cache any size of sub or nested object but the cache will require RAM and will grow with each new entry there is no cache eviction.

### Merging Sections

In many mappings from RDBMS to MongoDB there is a one to many relationship between one table an another - in our example each owner has many pets. Each pet has a single owner. In this case we can use a more efficient method to generate the data by sorting and merging the source tables. this is the option `mergeon`.

To do this we need to ensure both sections are sorted by the field we are using to join them. 'ownerid' in the owners tables and 'owner' in the pets table.

We can then use 'mergeon' to walk both tables merging the results.

_behind the scenes, MongoSyphon simply keeps the cursor open for the sub document and assumes any results it is looking for will be ordered at the point it previously left off_

Our pet's config, using this efficient merging mechanism, which avoids a query on the pets table per owner looks like this.

```
	ownerssection: {
		template: {
			_id: "$ownerid",
			name: "$name",
			address : "$address",
			pets : [ "@petsection" ]
		},
		query: { sql: 'SELECT *,ownerid as owner FROM owner order by ownerid'}

	},


	petsection: {
		template: {
			petid: "$petid",
			name: "$name",
		},
		query: { sql: 'SELECT * FROM pet order by owner'},
		mergeon:  "owner" 

	},

```

Currently MongoSyphon only merges on a field with the same name, therefore we need to alias the field ownerid to owner using SQL. In future this will allow merging on multiple columns with different names.

This typically requires you have an index on the column being sorted however that would also be required for the query based method above and woudl typically exist for retrieval anyway.

### Pushing transformation work  to SQL

All the previous examples have used simple SQL statements against a single database. The reason the JOIN operations have not been pushed ot the underlying database is that there is rarely a good and efficient way to do that for a 1 to Many relationship. You either have to retrieve repeated columns for many rows or use something like MySQL's **GROUP_CONCAT** function and then parse a text string.

For Many to Many and One to One relationships, sometimes it is better to push some logic into the SQL query and MongoSyphon imploses no limits on this other than your own SQL abilities.

We have seen an example of renaming a field above using *AS* where we wanted a column with a different name,

`sql: 'SELECT *,ownerid as owner FROM owner order by ownerid'`

We might also want to perform an operation like a sum or concatenation at the RDBMS side.

`sql: 'SELECT *,LEFT(telno,3) AS AREACODE,MID(telno,3,7) as DIGITS'`

Finally we can use the RDBMS to perform a server side **JOIN** where it is appropriate using any SQL syntax. Again correct indexing is vital to the performance of this but it *may* still be faster than using MongoSyphon to perform the join.

```
sql: 'SELECT ACTOR.NAME as NAME ,ACTORS2MOVIES.ROLE AS ROLE
		 FROM ACTORS,ACTORS2MOVIES WHERE MOVIEID=? AND 
		 ACTORS.ACTORID=ACTORS2MOVIES.ACTORID'
```

### Java transformations

Mongosyphon now supports document transformations which can be performed in Java code via a series of transformers which can be applied
prior to the document being inserted or updated. The transformer can be any Java class which implements the `com.johnlpage.mongosyphon.IDocumentTransformer` interface and has a default / no-args constructor. The final source document before insertion will be
passed to the `transform()` method, which should perform any modifications desired on that source document. Below is a very basic example of
a transformer which will add a new field to the document called `fullNameUpper` consisting of the upper-cased contentation of the document
fields `firstName` and `lastName`. The transformer will also remove the document field `somethingExtra`.

```
package com.johnlpage.mongosyphon.transformer.custom;
import org.bson.Document;
import com.johnlpage.mongosyphon.IDocumentTransformer;

public class TestTransform implements IDocumentTransformer {

    public void transform(Document source) {
        String firstName = source.getString("firstName");
        String lastName = source.getString("lastName");
        String fullNameUpper = (firstName + " " + lastName).toUpperCase(); 
        source.put("fullNameUpper", fullNameUpper);
        source.remove("somethingExtra");
    }
}
```

Configure the transformer(s) via the `documentTransformers` element, for example: 

```
{
    start: {
        source: {...},
        target: {...},
        template: {...},
        query:{...},
        documentTransformers: [
            {className: "com.johnlpage.mongosyphon.transformer.custom.TestTransform"}
        ]
    },
    ...
}
```

**NOTE** that transformers are currently only applied to top level document and will not be applied/processed for any subsection. This allows the entire document to be modified in it's final state (after subsections have been applied) before being output.

### Updates and Inserts

All previous examples have used the target `mode` of insert.

It is possible to use MongoSyphon to update an existing MongoDB database as well. To do so this use `mode: "update"`, `mode: "upsert"`,
or `mode: "save"` as target level parameters depending on the behaviour you desire.

The `mode: "save"` option follows the same semantics that `save()` uses in the mongo shell or driver methods. If the document 
provided does not contain an `_id` field, then an insert will be performed. Otherwise, if the document contains an `_id` field
an upsert will be performed which will either insert a new document (if one does not exist) or overwrite an existing document.

When using `mode: "update"` or `mode: "upsert"` the format of the top level document is different, it must be of the form:

```
someupdatesection: {
	template: {
	  "$find" : { ... },
	  "$set" : { ... },
	  "$inc" : { ... },
	  "$push" : {...}
	},
query: { sql: 'select X from Y' }
}
```
The template should correspond to a MongoDB update document specification using one of more update operators. The exception is the "$find" operator which specifies which *single* document to find an update. Currently multi-update is not supported.

By using the $ operator you can create update specifications which query an RDBMS table and then push updates into nested documents and arrays in MongoDB. For example if we now had a collection mapping pet_id's to microchip numbers we could push those through to update the pet records, or push them unto an array under the owner.

This is a very powerful feature but will take some thinking about to gain maximum benefit.


### Reading from MongoDB

Any section can also query MongoDB, so do this, in place of the *sql* option we use a *mongoquery* option like this


```
query: { 
            database: "test",
            collection: "atest" ,
            find: { },
            project : { },
            limit : 1,
            sort: { }
        }
```

or

```
mongoquery: { 
            database: "test",
            collection: "atest" ,
            aggregate: [{$match:{}},
                        {$group:{_id:null,x:{$sum:"$x"}}}]
        }
```

This can be used to convert or move data within  or between MongoDB systems  - including all the Join and Merge operators. It can also be used with an  RDBMS or Multiple MongoDB systems to merge data from multiple sources.

MongoDB queries are parameterised using $1 to $9 in the find() part or the $match part for queries and aggregation respectively. This is equivalent to using ? in the SQL query.

### Performing an initial query

Mongosyphon and run a qury agains either the RDBMS or MongoDB without outputting the records to act as parameters for a section that does output data. As a very simple example. Assume you are converting 10 Million records - they will be streamed from the RDBMS to MongoDB by MongoSyphon.

This stops part way through for some reason and you have N million converted.

As long as when you converted them you were sorting by some useful key, perhaps the primary key. With the Mongo Query facility you can query mongodb for the highest primary key it has loaded and continue from there. This makes it restartable but also allows for ongoing migration where you have an incrementing serial number of datestamped changes.

The MongoDB Query is run against the top level collection you are loading, it returns a single Document which is treated like an RDBMS row. It is configured using three top level paramaeters.

The examples below simply mean, find the highest _id value in the Mongo Database



Example, assuming in the source *movieid* is a sequence, find any new movies. So query all movies, order by _id in reverse adn take the first one. If nothing found then return an _id value of -1


```


start : {
    source: {
        uri: "mongodb://MacPro.local:27017/"
    },
    query: { 
        database: "imdb",
        collection: "movies" ,
        find: {} ,
        limit: 1,
        project: { _id: 1},
        sort: { _id : -1},
        default : { _id: 0 }
    },
    target : {
        mode: "subsection",
        uri: "moviesection",
      }
},

"moviessection": {
    source: {	
    	uri:  "jdbc:mysql://localhost:3306/sdemo?useSSL=false",
	user: "root",
	password: "password"
	},
   "template": {
      "_id": "$movieid",
      "title": "$title",
      "year": "$year",
       },
    query :{"sql": "SELECT * FROM movies where movieid > ? order by movieid"}
    "params" : [ "_id" ]
  },
```


Using this currently may require some MongoDB schema changes to support it - for example:

Given our owners and pets scenario - new pets are added to the RDBMS - to determine the last petid we saw we need to query MongoDB for the max petid which is in a subdocument.

To do this we can use an aggregation pipeline or a view built on one but that will not be fast.

In each document (owner) we can add a top level field (maxpetid) calculated using it's own section on initial load and using $max on subsequent updates. We can index and sort on this as we are doing above for _id. This is the current best approach. but needs another field and index to support it.

We can run an additional top level update to calculate and retain the max value of *petid* in a different collection , as long as we can specify a collection for the Mongo Query. This shoudl be bound into the same config though once multiple parallel conversion are allowed.




### Logging and output

Logging is handled by logback and configured in the logback xml either in the JAR file or supplied at runtime. By default logging data is verbose and goes to MongoSyphon.log in the directory you are running MongoSyphon.

