{
sourceConnection: "jdbc:mysql://localhost:3306/employees?useUnicode=true&useServerPrepStmts=true&useSSL=false",
sourceUser: "root",
sourcePassword: "password",
mongoDestConnection: "mongodb://MacPro.local:27017/",
mongoDestDatabase: "employees",
mongoDestCollection: "dept_emp_latest_date",

startAt: "dept_emp_latest_datesection",

  "departmentssection": {
    "template": {
      "dept_no": "$dept_no",
      "dept_name": "$dept_name"
    },
    "sql": "SELECT * FROM departments "
  },
  "dept_empsection": {
    "template": {
      "emp_no": "$emp_no",
      "dept_no": "$dept_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    "sql": "SELECT * FROM dept_emp "
  },
  "dept_managersection": {
    "template": {
      "emp_no": "$emp_no",
      "dept_no": "$dept_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    "sql": "SELECT * FROM dept_manager "
  },
  "employeessection": {
    "template": {
      "emp_no": "$emp_no",
      "birth_date": "$birth_date",
      "first_name": "$first_name",
      "last_name": "$last_name",
      "gender": "$gender",
      "hire_date": "$hire_date"
    },
    "sql": "SELECT * FROM employees "
  },
  "salariessection": {
    "template": {
      "emp_no": "$emp_no",
      "salary": "$salary",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    "sql": "SELECT * FROM salaries "
  },
  "titlessection": {
    "template": {
      "emp_no": "$emp_no",
      "title": "$title",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    "sql": "SELECT * FROM titles "
  },
  "current_dept_empsection": {
    "template": {
      "emp_no": "$emp_no",
      "dept_no": "$dept_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    "sql": "SELECT * FROM current_dept_emp "
  },
  "dept_emp_latest_datesection": {
    "template": {
      "emp_no": "$emp_no",
      "from_date": "$from_date",
      "to_date": "$to_date"
    },
    "sql": "SELECT * FROM dept_emp_latest_date "
  }
}
