# **Project Configuration Guide**

## **Setting Up Configuration**

### Before running the project, you need to configure the database connection settings. Follow these steps:

1. Locate the appsettings.example.json file in the resources directory.

2. Rename appsettings.example.json to appsettings.json.

3. Open appsettings.json and replace the placeholder values with your actual database credentials:

`{`
`    "db": {`
`        "MySql": {`
`        "host": "localhost",`
`        "port": 3306,`
`        "database": "replace_with_your_database_schema_name_here",`
`        "user": "replace_with_your_user_name_here",`
`        "password": "replace_with_your_user_password_here"`
`        }`
`    },`
`    "token": {`
`        "lifetime": 300`
`    }`
`}`

4. Do not commit appsettings.json to the repository. It contains sensitive credentials.

5. The .gitignore file already excludes appsettings.json to prevent accidental commits.

## Running the Project

* After setting up the configuration, you can run the project as usual.

* Ensure that your MySQL database is running with the specified credentials.

* Start the application and verify the connection in the logs.

* If you encounter any issues, check the logs for detailed error messages.

