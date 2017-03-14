{
databaseConnection: "jdbc:mysql://localhost:3306/phones?useUnicode=true&useServerPrepStmts=true&useSSL=false",
databaseUser: "root",
databasePassword: "password",
mongoConnection: "mongodb://MacPro.local:27017/",
mongoDatabase: "test",
mongoCollection: "phones",

startAt: "people",

people: {
  template: {
      "_id": "$id",
      "name": "$name",
      "phones" : [ "@phones" ]
    },
  sql: 'SELECT * FROM people order by id',
  }
  
phones: {
 	sql: 'SELECT *,owner as id FROM phones order by owner',
 	template: {
      "digits": "$digits",
      "make": "$make",
      "model" : "$model",
      "owner": "$owner"
    },
    "mergeon": "id"
 }
 
}