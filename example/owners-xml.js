{
	databaseConnection: "jdbc:mysql://localhost:3306/sdemo?useSSL=false",
	databaseUser: "root",
	databasePassword: "password",
	mongoConnection: "mongodb://localhost:27017/",
	mongoDatabase: "sdemo",
	mongoCollection: "owners",
	mode: "XML",
	startAt: "ownerssection",


	ownerssection: {
		template: {
			_id: "$ownerid",
			name: "$name",
			address : "$address",
			pets : [ "@petsection" ]
		},
		sql: 'SELECT * FROM owner',

	},


	petsection: {
		template: {
			petid: "$petid",
			name: "$name",
			species : "@speciessection"
		},
		sql: 'SELECT * FROM pet where owner = ?',
		params: [ "ownerid" ]

	},

	speciessection: {
		template: {
			_value : "$species"
		},
		sql: 'SELECT * from species where speciesid = ?',
		params : [ "species" ]

	}

}