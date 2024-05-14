# PHP Slim API CRUD Generator

The PHP Slim API CRUD Generator is a Java-based desktop application that automates the generation of CRUD (Create, Read, Update, Delete) functionality for a PHP Slim API using a MySQL database. It simplifies the process of creating APIs and database operations by automatically generating the necessary code based on the selected database tables.

## Features

- Connects to a MySQL database and retrieves a list of tables
- Allows selecting specific tables for CRUD generation
- Generates PHP Slim controllers, models, and services for each selected table
- Creates MySQL queries for CRUD operations
- Provides a user-friendly GUI for easy configuration and generation

## Prerequisites

Before running the CRUD Generator, ensure that you have the following:

- Java Development Kit (JDK) installed
- MySQL database with the required tables

## Getting Started

1. Clone the repository or download the source code files.

2. Open the project in your preferred Java IDE.

3. Run the `App` class and fill out the database connection details in the GUI form:
   - Enter the values for `Host`, `Port`, `Username`, `Password`, and `Database` to match your MySQL database configuration.

4. In the CRUD Generator GUI:
   - Enter the package name for the generated code in the "Package Name" field.
   - Click the "Connect" button to establish a connection to the MySQL database.
   - Select the desired tables from the list of available tables.
   - Click the "Generate API" button to generate the CRUD code.

5. The generated code will be saved in the specified package directory.

## Generated Code Structure

The CRUD Generator creates the following code structure:

- PHP Slim Controllers:
  - `[TableName]Controller.php`: Controller for handling API endpoints for the table.

- PHP Models:
  - `[TableName].php`: Model class representing the table structure.

- PHP Services:
  - `[TableName]Service.php`: Service class for handling business logic and database operations.

- Routes:
  - `api.php`: Slim framework routes configuration for the generated endpoints.

- .env File:
  - `.env`: Environment configuration file for database and application settings.

## Dependencies

The CRUD Generator utilizes the following dependencies:

- Java Swing: For creating the GUI components.
- MySQL Connector/J: For connecting to the MySQL database.
- Regex: For processing and manipulating table and column names.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgements

The PHP Slim API CRUD Generator was developed by [Lance](https://www.lance.name).
