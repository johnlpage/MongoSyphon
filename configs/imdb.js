{


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

"moviesection": {
    source: {
        uri:  "jdbc:mysql://localhost:3306/jmdb?useUnicode=true&useServerPrepStmts=true&useSSL=false",
        user: "root",
        password: "password",
    },
    target : {
      mode: "json",

    },
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
    "query": {sql:"SELECT * FROM movies where movieid > ? order by movieid"},
    "params" : [ "_id" ]
  },

   "akatitlessection": {
    "template": {
      "language": "$language",
      "title": "$title",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM akatitles where movieid>? order by movieid"},
    "mergeon":"movieid",
    "params": ["movieid"]
  },

   "altversionssection": {
    "template": {
      "_value": "$versiontext"
    },
    "query": {sql:"SELECT * FROM altversions where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },

 "businesssection": {
    "template": {
      "_value": "$businesstext"
    },
    "query": {sql:"SELECT * FROM business  where movieid>? order by movieid"},
    "mergeon":"movieid",
    "params": ["movieid"]
  },

   "certificatessection": {
    "template": {
      "country": "$country",
      "certification": "$certification",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM certificates  where movieid>? order by movieid"},
    "mergeon":"movieid",
    "params": ["movieid"]
  },
  
    "colorinfosection": {
    "template": {
      "color": "$color",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM colorinfo  where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  
  "countriessection": {
      "template": {
        "_value": "$country"
      },
      "query": {sql:"SELECT * FROM countries  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
    },
    "crazycreditssection": {
    "template": {
    
      "credittext": "$credittext"
    },
    "query": {sql:"SELECT * FROM crazycredits  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  
  "distributorssection": {
      "template": {
        "name": "$name",
        "addition": "$addition"
      },
      "query": {sql:"SELECT * FROM distributors  where movieid>?   order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
    },
   "genressection": {
    "template": {
      "_value": "$genre"
    },
    "query": {sql:"SELECT * FROM genres  where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "goofssection": {
    "template": {
      "_value": "$gooftext"
    },
    "query": {sql:"SELECT * FROM goofs  where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "keywordssection": {
    "template": {
      "_value": "$keyword"
    },
    "query": {sql:"SELECT * FROM keywords  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "languagesection": {
    "template": {
      "language": "$language",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM language  where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "literaturesection": {
    "template": {
      "_value": "$literaturetext"
    },
    "query": {sql:"SELECT * FROM literature  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "locationssection": {
    "template": {

      "location": "$location",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM locations  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
    
  },
   "movielinkssection": {
    "template": {
      "_value": "$movielinkstext"
    },
    "query": {sql:"SELECT * FROM movielinks  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  
  "mpaaratingssection": {
    "template": {
      "_value": "$reasontext"
    },
    "query": {sql:"SELECT * FROM mpaaratings  where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "plotssection": {
    "template": {
      "_value": "$plottext"
    },
    "query": {sql:"SELECT * FROM plots  where movieid>?   order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "prodcompaniessection": {
    "template": {
      "name": "$name",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM prodcompanies   where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
   "quotessection": {
    "template": {
      "_value": "$quotetext"
    },
    "query": {sql:"SELECT * FROM quotes  where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
    "ratingssection": {
    "template": {
      "rank": "$rank",
      "votes": "$votes",
      "distribution": "$distribution"
    },
    "query": {sql:"SELECT * FROM ratings  where movieid>?  order by movieid"},
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
    "query": {sql:"SELECT * FROM releasedates  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "runningtimessection": {
    "template": {
      "time": "$time",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM runningtimes  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "soundmixsection": {
    "template": {
      "sound": "$sound",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM soundmix  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "soundtrackssection": {
    "template": {
      "_value": "$soundtracktext"
    },
    "query": {sql:"SELECT * FROM soundtracks  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "taglinessection": {
    "template": {
      "_value": "$taglinetext"
    },
    "query": {sql:"SELECT * FROM taglines  where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "technicalsection": {
    "template": {

      "name": "$name",
      "addition": "$addition"
    },
    "query": {sql:"SELECT * FROM technical where movieid>?  order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  },
  "triviasection": {
    "template": {

      "_value": "$triviatext"
    },
    "query": {sql:"SELECT * FROM trivia   where movieid>? order by movieid"},
     "mergeon":"movieid",
     "params": ["movieid"]
  }
  
}
