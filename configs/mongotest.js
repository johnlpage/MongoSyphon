{

    start: {
        source: {
            uri: "mongodb://MacPro.local:27017",
            user: "",
            password: ""
        },
        target : {
          mode: "JSON",
          uri: "mongodb://MacPro.local:27017/",
          namespace: "test.mongoout"
        },
        template: {
            "id": "$_id",
            "name": "$name",
            "favcolour" : "$favcolour",
            "favcolourname" : "@coloursection"
        },
        query: { 
            database: "test",
            collection: "people" ,
            find: {}
        }
    },
    
    coloursection: {
        cached : true,
    
        template: {
            _value: "$name"
        },
        query: { 
            database: "test",
            collection: "colours" ,
            find: { _id : "$1"} 
        },
        params: [ "favcolour" ]

    }
}

