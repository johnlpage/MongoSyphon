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
    "sql": "SELECT * FROM akatitles order by movieid",
    "mergeon":"movieid"

  },

   "altversionssection": {
    "template": {
      "_value": "$versiontext"
    },
    "sql": "SELECT * FROM altversions order by movieid",
     "mergeon":"movieid"
  },

 "businesssection": {
    "template": {
      "_value": "$businesstext"
    },
    "sql": "SELECT * FROM business order by movieid",
    "mergeon":"movieid"
  },

   "certificatessection": {
    "template": {
      "country": "$country",
      "certification": "$certification",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM certificates order by movieid",
    "mergeon":"movieid"
  },
  
    "colorinfosection": {
    "template": {
      "color": "$color",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM colorinfo order by movieid",
     "mergeon":"movieid"
  },
  
  "countriessection": {
      "template": {
        "_value": "$country"
      },
      "sql": "SELECT * FROM countries  order by movieid",
     "mergeon":"movieid"
    },
    "crazycreditssection": {
    "template": {
    
      "credittext": "$credittext"
    },
    "sql": "SELECT * FROM crazycredits  order by movieid",
     "mergeon":"movieid"
  },
  
  "distributorssection": {
      "template": {
        "name": "$name",
        "addition": "$addition"
      },
      "sql": "SELECT * FROM distributors   order by movieid",
     "mergeon":"movieid"
    },
   "genressection": {
    "template": {
      "_value": "$genre"
    },
    "sql": "SELECT * FROM genres order by movieid",
     "mergeon":"movieid"
  },
  "goofssection": {
    "template": {
      "_value": "$gooftext"
    },
    "sql": "SELECT * FROM goofs order by movieid",
     "mergeon":"movieid"
  },
  "keywordssection": {
    "template": {
      "_value": "$keyword"
    },
    "sql": "SELECT * FROM keywords  order by movieid",
     "mergeon":"movieid"
  },
   "languagesection": {
    "template": {
      "language": "$language",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM language order by movieid",
     "mergeon":"movieid"
  },
   "literaturesection": {
    "template": {
      "_value": "$literaturetext"
    },
    "sql": "SELECT * FROM literature  order by movieid",
     "mergeon":"movieid"
  },
   "locationssection": {
    "template": {

      "location": "$location",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM locations  order by movieid",
     "mergeon":"movieid"
    
  },
   "movielinkssection": {
    "template": {
      "_value": "$movielinkstext"
    },
    "sql": "SELECT * FROM movielinks  order by movieid",
     "mergeon":"movieid"
  },
  
  "mpaaratingssection": {
    "template": {
      "_value": "$reasontext"
    },
    "sql": "SELECT * FROM mpaaratings order by movieid",
     "mergeon":"movieid"
  },
  "plotssection": {
    "template": {
      "_value": "$plottext"
    },
    "sql": "SELECT * FROM plots   order by movieid",
     "mergeon":"movieid"
  },
  "prodcompaniessection": {
    "template": {
      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM prodcompanies   order by movieid",
     "mergeon":"movieid"
  },
   "quotessection": {
    "template": {
      "_value": "$quotetext"
    },
    "sql": "SELECT * FROM quotes order by movieid",
     "mergeon":"movieid"
  },
    "ratingssection": {
    "template": {
      "rank": "$rank",
      "votes": "$votes",
      "distribution": "$distribution"
    },
    "sql": "SELECT * FROM ratings  order by movieid",
     "mergeon":"movieid"
  },
  "releasedatessection": {
    "template": {
      "country": "$country",
      "imdbdate": "$imdbdate",
      "releasedate": "$releasedate",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM releasedates  order by movieid",
     "mergeon":"movieid"
  },
  "runningtimessection": {
    "template": {
      "time": "$time",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM runningtimes  order by movieid",
     "mergeon":"movieid"
  },
  "soundmixsection": {
    "template": {
      "sound": "$sound",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM soundmix  order by movieid",
     "mergeon":"movieid"
  },
  "soundtrackssection": {
    "template": {
      "_value": "$soundtracktext"
    },
    "sql": "SELECT * FROM soundtracks  order by movieid",
     "mergeon":"movieid"
  },
  "taglinessection": {
    "template": {
      "_value": "$taglinetext"
    },
    "sql": "SELECT * FROM taglines  order by movieid",
     "mergeon":"movieid"
  },
  "technicalsection": {
    "template": {

      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM technical order by movieid",
     "mergeon":"movieid"
  },
  "triviasection": {
    "template": {

      "_value": "$triviatext"
    },
    "sql": "SELECT * FROM trivia  order by movieid",
     "mergeon":"movieid"
  }
  
}
