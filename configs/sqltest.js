{
    sourceConnection: "jdbc:mysql://localhost:3306/test?useUnicode=true&useServerPrepStmts=true&useSSL=false",
    sourceUser: "root",
    sourcePassword: "password",

    mongoDestConnection: "mongodb://MacPro.local:27017/",
    mongoDestDatabase: "test",
    mongoDestCollection: "mongoout",
    mode: "json",

    startAt: "people",

    people: {
        template: {
            "id": "$id",
            "name": "$name",
            "favcolour" : "$favcolour",
            "favcolourname" : "@coloursection"
        },
       sql: "select * from people"
    },
    coloursection: {
        template: {
            name: "$name"
        },
        sql: "SELECT * FROM colours WHERE id=?",
        params: [ "favcolour" ]
    }
}

