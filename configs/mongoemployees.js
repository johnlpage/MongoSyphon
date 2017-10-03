{

    
start: {
  template: {
      "_id": "$emp_no",
      "birth_date": "$birth_date",
      "first_name": "$first_name",
      "last_name": "$last_name",
      "gender": "$gender",
      "hire_date": "$hire_date",
      "titles" : [ "@titlessection" ],
      "salaries": [ "@salariessection"],
      "depts": [ "@dept_empsection"],
    },
    source: {
        uri: "mongodb://MacPro.local:27017",
    },
    target : {
      mode: "insert",
      uri: "mongodb://MacPro.local:27017/",
      namespace: "test.employees"
    },
    query: { 
        database: "employees",
        collection: "employees" ,
        find: {} ,
        project: { last_name: 0},
        sort: { emp_no : 1}
    }
},


titlessection: {
  template: {
      "title": "$title",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    query: { 
        database: "employees",
        collection: "titles" ,
        find: {} ,
        sort: { emp_no : 1}
    },
  mergeon: "emp_no"
  },



salariessection:{
  template: {
      "salary": "$salary",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    query: { 
        database: "employees",
        collection: "salaries",
        find: {} ,
        sort: { emp_no : 1}
    },
 mergeon: "emp_no" 
},


dept_empsection:{
  template: {
      "department" : "@departmentssection",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    query: { 
        database: "employees",
        collection: "dept_emp" ,
        find: {} ,
        sort: { emp_no : 1}
    },
   mergeon: "emp_no" 
},

departmentssection: {
 template: {
      "_value": "$dept_name"
    },
    query: { 
        database: "employees",
        collection: "departments" ,
        find: { dept_no : "$1 "} 
    },
 
  params: [ "dept_no" ],
  cached: true
}



}
