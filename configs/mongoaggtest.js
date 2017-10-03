{
    sourceUser: "",
    sourcePassword: "",
    sourceConnection: "mongodb://MacPro.local:27017",
    
    mongoDestConnection: "mongodb://MacPro.local:27017/",
    mongoDestDatabase: "test",
    mongoDestCollection: "employees"
        
    ouputMode: "json",

    startAt: "test",

    test: {
        template: {
            "id": "$_id",
            "x": "$x"
        },
        mongoquery: { 
            database: "test",
            collection: "atest" ,
            aggregate: [{$match:{}},
                        {$group:{_id:null,x:{$sum:"$x"}}}]
        }
    }
}
