{
"databaseConnection": "jdbc:mysql://localhost:3306/jmdb?useUnicode=true&useServerPrepStmts=true&useSSL=false",
"databaseUser": "root",
"databasePassword": "password",
"mongoConnection": "mongodb://MacPro.local:27017/",
"mongoDatabase": "imdb",
"mongoCollection": "movies",
"mode":"upsert",
"startAt": "moviessection",



"moviessection1": {
    "template": {
       "_id":"$movieid",
      "title" : "$title"
    },
    "sql": "SELECT * FROM movies order by movieid limit 10",
  },

"moviessection": {
    "template": {
      "$find": { "_id":"$movieid"},
      "$set": {  "some":"one" }
    },
    "sql": "SELECT * FROM movies order by movieid limit 11",
  }
  
}
