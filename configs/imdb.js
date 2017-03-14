{
"databaseConnection": "jdbc:mysql://localhost:3306/jmdb?useUnicode=true&useServerPrepStmts=true&useSSL=false",
"databaseUser": "root",
"databasePassword": "password",
"mongoConnection": "mongodb://MacPro.local:27017/",
"mongoDatabase": "imdb",
"mongoCollection": "movies",
"mongoQuery" : {},
"mongoFields" : { "_id" : 1},
"mongoOrderBy" : { "_id" : -1},
"mongoDefault" : { "_id" : 0 },
"startAt": "moviessection",

"moviessection": {
    "template": {
      "_id": "$movieid",
      "title": "$title",
      "year": "$year",
      "imdbid": "$imdbid",
      "akatitle": [ "@akatitlessection"],
      "altversions": [ "@altversionssection"],
      "business": [ "@businesssection"],
      "certificates": [ "@certificatessection"],
      "colorinfosection": [ "@certificatessection"],
      "crazycredits": [ "@crazycreditssection"],
      "distributors" : ["@distributorssection"],
      "genres" : ["@genressection"],
      "goofs" : ["@goofssection"],
      "keywords" : ["@keywordssection"],
      "languages" : ["@languagesection"],
      "literature":["@literaturesection"],
      "locations":["@locationssection"],
      "movielinks":["@movielinkssection"],
      "mparating":["@mpaaratingssection"],
      "plots":["@plotssection"],
      "prodcompanies":["@prodcompaniessection"],
      "quotes":["@quotessection"],
      "ratings":["@ratingssection"],
      "releasedates":["@releasedatessection"],
      "runningtime":["@runningtimessection"],
      "soundmix":["@soundmixsection"],
      "soundtrack":["@soundtrackssection"],
      "taglines":["@taglinessection"],
      "technical":["@technicalsection"],
      "trivia":["@triviasection"]
    },
    "sql": "SELECT * FROM movies where movieid > ? order by movieid",
    "params" : [ "_id" ]
  },

   "akatitlessection": {
    "template": {
      "language": "$language",
      "title": "$title",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM akatitles where movieid>? order by movieid",
    "mergeon":"movieid",
    "params": ["movieid"]
  },

   "altversionssection": {
    "template": {
      "_value": "$versiontext"
    },
    "sql": "SELECT * FROM altversions where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },

 "businesssection": {
    "template": {
      "_value": "$businesstext"
    },
    "sql": "SELECT * FROM business  where movieid>? order by movieid",
    "mergeon":"movieid",
    "params": ["movieid"]
  },

   "certificatessection": {
    "template": {
      "country": "$country",
      "certification": "$certification",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM certificates  where movieid>? order by movieid",
    "mergeon":"movieid",
    "params": ["movieid"]
  },
  
    "colorinfosection": {
    "template": {
      "color": "$color",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM colorinfo  where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  
  "countriessection": {
      "template": {
        "_value": "$country"
      },
      "sql": "SELECT * FROM countries  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
    },
    "crazycreditssection": {
    "template": {
    
      "credittext": "$credittext"
    },
    "sql": "SELECT * FROM crazycredits  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  
  "distributorssection": {
      "template": {
        "name": "$name",
        "addition": "$addition"
      },
      "sql": "SELECT * FROM distributors  where movieid>?   order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
    },
   "genressection": {
    "template": {
      "_value": "$genre"
    },
    "sql": "SELECT * FROM genres  where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "goofssection": {
    "template": {
      "_value": "$gooftext"
    },
    "sql": "SELECT * FROM goofs  where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "keywordssection": {
    "template": {
      "_value": "$keyword"
    },
    "sql": "SELECT * FROM keywords  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "languagesection": {
    "template": {
      "language": "$language",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM language  where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "literaturesection": {
    "template": {
      "_value": "$literaturetext"
    },
    "sql": "SELECT * FROM literature  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "locationssection": {
    "template": {

      "location": "$location",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM locations  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
    
  },
   "movielinkssection": {
    "template": {
      "_value": "$movielinkstext"
    },
    "sql": "SELECT * FROM movielinks  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  
  "mpaaratingssection": {
    "template": {
      "_value": "$reasontext"
    },
    "sql": "SELECT * FROM mpaaratings  where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "plotssection": {
    "template": {
      "_value": "$plottext"
    },
    "sql": "SELECT * FROM plots  where movieid>?   order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "prodcompaniessection": {
    "template": {
      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM prodcompanies   where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "quotessection": {
    "template": {
      "_value": "$quotetext"
    },
    "sql": "SELECT * FROM quotes  where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
    "ratingssection": {
    "template": {
      "rank": "$rank",
      "votes": "$votes",
      "distribution": "$distribution"
    },
    "sql": "SELECT * FROM ratings  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "releasedatessection": {
    "template": {
      "country": "$country",
      "imdbdate": "$imdbdate",
      "releasedate": "$releasedate",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM releasedates  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "runningtimessection": {
    "template": {
      "time": "$time",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM runningtimes  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "soundmixsection": {
    "template": {
      "sound": "$sound",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM soundmix  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "soundtrackssection": {
    "template": {
      "_value": "$soundtracktext"
    },
    "sql": "SELECT * FROM soundtracks  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "taglinessection": {
    "template": {
      "_value": "$taglinetext"
    },
    "sql": "SELECT * FROM taglines  where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "technicalsection": {
    "template": {

      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM technical where movieid>?  order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "triviasection": {
    "template": {

      "_value": "$triviatext"
    },
    "sql": "SELECT * FROM trivia   where movieid>? order by movieid",
     "mergeon":"movieid",
     "params": ["movieid"]
  }
  
}
