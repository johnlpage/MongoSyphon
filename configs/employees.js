{
sourceConnection: "jdbc:mysql://localhost:3306/employees?useUnicode=true&useServerPrepStmts=true&useSSL=false",
sourceUser: "root",
sourcePassword: "password",
mongoConnection: "mongodb://MacPro.local:27017/",
mongoDatabase: "test",
mongoCollection: "employees",

startAt: "employeessection",


employeessection: {
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
  sql: 'SELECT * FROM employees order by emp_no'
},


titlessection: {
  template: {
      "title": "$title",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
  sql: 'SELECT * FROM titles order by emp_no',
  mergeon: "emp_no"
  },



salariessection:{
  template: {
      "salary": "$salary",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
  sql: 'SELECT * FROM salaries order by emp_no',
 mergeon: "emp_no" 
},


dept_empsection:{
  template: {
      "department" : "@departmentssection",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
  sql: 'SELECT * FROM dept_emp order by emp_no',
   mergeon: "emp_no" 
},

departmentssection: {
 template: {
      "_value": "$dept_name"
    },
  sql: 'SELECT * FROM departments where dept_no=? ',
  params: [ "dept_no" ],
  cached: true
},



current_dept_empsection: {
  template: {
      "emp_no": "$emp_no",
      "dept_no": "$dept_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
  sql: 'SELECT * FROM current_dept_emp ',
},

dept_emp_latest_datesection: { 
 template: {
      "emp_no": "$emp_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
  sql: 'SELECT * FROM dept_emp_latest_date ',
},

dept_managersection:{
 template:{
      "emp_no": "$emp_no",
      "dept_no": "$dept_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
  sql: 'SELECT * FROM dept_manager '
}
}
