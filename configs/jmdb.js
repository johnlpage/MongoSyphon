{
  "actorssection": {
    "template": {
      "actorid": "$actorid",
      "name": "$name",
      "sex": "$sex"
    },
    "sql": "SELECT * FROM actors "
  },
  "akanamessection": {
    "template": {
      "name": "$name",
      "akaname": "$akaname",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM akanames "
  },
  "akatitlessection": {
    "template": {
      "movieid": "$movieid",
      "language": "$language",
      "title": "$title",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM akatitles "
  },
  "altversionssection": {
    "template": {
      "movieid": "$movieid",
      "versiontext": "$versiontext"
    },
    "sql": "SELECT * FROM altversions "
  },
  "biographiessection": {
    "template": {
      "bioid": "$bioid",
      "name": "$name",
      "biotext": "$biotext"
    },
    "sql": "SELECT * FROM biographies "
  },
  "businesssection": {
    "template": {
      "movieid": "$movieid",
      "businesstext": "$businesstext"
    },
    "sql": "SELECT * FROM business "
  },
  "certificatessection": {
    "template": {
      "movieid": "$movieid",
      "country": "$country",
      "certification": "$certification",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM certificates "
  },
  "cinematgrssection": {
    "template": {
      "cinematid": "$cinematid",
      "name": "$name"
    },
    "sql": "SELECT * FROM cinematgrs "
  },
  "colorinfosection": {
    "template": {
      "movieid": "$movieid",
      "color": "$color",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM colorinfo "
  },
  "composerssection": {
    "template": {
      "composerid": "$composerid",
      "name": "$name"
    },
    "sql": "SELECT * FROM composers "
  },
  "costdesignerssection": {
    "template": {
      "costdesid": "$costdesid",
      "name": "$name"
    },
    "sql": "SELECT * FROM costdesigners "
  },
  "countriessection": {
    "template": {
      "movieid": "$movieid",
      "country": "$country"
    },
    "sql": "SELECT * FROM countries "
  },
  "crazycreditssection": {
    "template": {
      "movieid": "$movieid",
      "credittext": "$credittext"
    },
    "sql": "SELECT * FROM crazycredits "
  },
  "directorssection": {
    "template": {
      "directorid": "$directorid",
      "name": "$name"
    },
    "sql": "SELECT * FROM directors "
  },
  "distributorssection": {
    "template": {
      "movieid": "$movieid",
      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM distributors "
  },
  "editorssection": {
    "template": {
      "editorid": "$editorid",
      "name": "$name"
    },
    "sql": "SELECT * FROM editors "
  },
  "genressection": {
    "template": {
      "movieid": "$movieid",
      "genre": "$genre"
    },
    "sql": "SELECT * FROM genres "
  },
  "goofssection": {
    "template": {
      "movieid": "$movieid",
      "gooftext": "$gooftext"
    },
    "sql": "SELECT * FROM goofs "
  },
  "keywordssection": {
    "template": {
      "movieid": "$movieid",
      "keyword": "$keyword"
    },
    "sql": "SELECT * FROM keywords "
  },
  "languagesection": {
    "template": {
      "movieid": "$movieid",
      "language": "$language",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM language "
  },
  "literaturesection": {
    "template": {
      "movieid": "$movieid",
      "literaturetext": "$literaturetext"
    },
    "sql": "SELECT * FROM literature "
  },
  "locationssection": {
    "template": {
      "movieid": "$movieid",
      "location": "$location",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM locations "
  },
  "miscsection": {
    "template": {
      "miscid": "$miscid",
      "name": "$name"
    },
    "sql": "SELECT * FROM misc "
  },
  "movielinkssection": {
    "template": {
      "movieid": "$movieid",
      "movielinkstext": "$movielinkstext"
    },
    "sql": "SELECT * FROM movielinks "
  },
  "moviessection": {
    "template": {
      "movieid": "$movieid",
      "title": "$title",
      "year": "$year",
      "imdbid": "$imdbid"
    },
    "sql": "SELECT * FROM movies "
  },
  "movies2actorssection": {
    "template": {
      "movieid": "$movieid",
      "actorid": "$actorid",
      "as_character": "$as_character"
    },
    "sql": "SELECT * FROM movies2actors "
  },
  "movies2cinematgrssection": {
    "template": {
      "movieid": "$movieid",
      "cinematid": "$cinematid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2cinematgrs "
  },
  "movies2composerssection": {
    "template": {
      "movieid": "$movieid",
      "composerid": "$composerid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2composers "
  },
  "movies2costdessection": {
    "template": {
      "movieid": "$movieid",
      "costdesid": "$costdesid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2costdes "
  },
  "movies2directorssection": {
    "template": {
      "movieid": "$movieid",
      "directorid": "$directorid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2directors "
  },
  "movies2editorssection": {
    "template": {
      "movieid": "$movieid",
      "editorid": "$editorid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2editors "
  },
  "movies2miscsection": {
    "template": {
      "movieid": "$movieid",
      "miscid": "$miscid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2misc "
  },
  "movies2proddessection": {
    "template": {
      "movieid": "$movieid",
      "proddesid": "$proddesid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2proddes "
  },
  "movies2producerssection": {
    "template": {
      "movieid": "$movieid",
      "producerid": "$producerid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2producers "
  },
  "movies2writerssection": {
    "template": {
      "movieid": "$movieid",
      "writerid": "$writerid",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM movies2writers "
  },
  "mpaaratingssection": {
    "template": {
      "movieid": "$movieid",
      "reasontext": "$reasontext"
    },
    "sql": "SELECT * FROM mpaaratings "
  },
  "plotssection": {
    "template": {
      "movieid": "$movieid",
      "plottext": "$plottext"
    },
    "sql": "SELECT * FROM plots "
  },
  "prodcompaniessection": {
    "template": {
      "movieid": "$movieid",
      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM prodcompanies "
  },
  "proddesignerssection": {
    "template": {
      "proddesid": "$proddesid",
      "name": "$name"
    },
    "sql": "SELECT * FROM proddesigners "
  },
  "producerssection": {
    "template": {
      "producerid": "$producerid",
      "name": "$name"
    },
    "sql": "SELECT * FROM producers "
  },
  "quotessection": {
    "template": {
      "movieid": "$movieid",
      "quotetext": "$quotetext"
    },
    "sql": "SELECT * FROM quotes "
  },
  "ratingssection": {
    "template": {
      "movieid": "$movieid",
      "rank": "$rank",
      "votes": "$votes",
      "distribution": "$distribution"
    },
    "sql": "SELECT * FROM ratings "
  },
  "releasedatessection": {
    "template": {
      "movieid": "$movieid",
      "country": "$country",
      "imdbdate": "$imdbdate",
      "releasedate": "$releasedate",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM releasedates "
  },
  "runningtimessection": {
    "template": {
      "movieid": "$movieid",
      "time": "$time",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM runningtimes "
  },
  "soundmixsection": {
    "template": {
      "movieid": "$movieid",
      "sound": "$sound",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM soundmix "
  },
  "soundtrackssection": {
    "template": {
      "movieid": "$movieid",
      "soundtracktext": "$soundtracktext"
    },
    "sql": "SELECT * FROM soundtracks "
  },
  "taglinessection": {
    "template": {
      "movieid": "$movieid",
      "taglinetext": "$taglinetext"
    },
    "sql": "SELECT * FROM taglines "
  },
  "technicalsection": {
    "template": {
      "movieid": "$movieid",
      "name": "$name",
      "addition": "$addition"
    },
    "sql": "SELECT * FROM technical "
  },
  "triviasection": {
    "template": {
      "movieid": "$movieid",
      "triviatext": "$triviatext"
    },
    "sql": "SELECT * FROM trivia "
  },
  "writerssection": {
    "template": {
      "writerid": "$writerid",
      "name": "$name"
    },
    "sql": "SELECT * FROM writers "
  }
}
