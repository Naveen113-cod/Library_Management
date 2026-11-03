# Library Management System

A small Java console application that stores books in MySQL using JDBC.

## Project files

- `DBConnection.java` - opens the MySQL connection.
- `Book.java` - holds a book's ID, title, author, and issue status.
- `Main.java` - displays the menu and runs the database operations.
- `schema.sql` - creates the database and books table.

## Requirements

- JDK 17 or newer.
- MySQL Server.
- MySQL Connector/J (the JDBC driver).
- VS Code with the **Extension Pack for Java** installed.

## Set up the database

1. Start MySQL Server.
2. Open `schema.sql` in MySQL Workbench and run it. It creates the `library_db` database and `books` table.
3. Open `DBConnection.java` and set `USER` and `PASSWORD` to your MySQL username and password.
   The default username is `root`; replace `your_mysql_password` with your actual password.

## Run from VS Code on Windows

1. Download the **Platform Independent** ZIP for MySQL Connector/J from
   [MySQL Connector/J downloads](https://dev.mysql.com/downloads/connector/j/).
2. Extract the ZIP and copy its `mysql-connector-j-<version>.jar` file into this project folder.
3. Open this folder in VS Code. Open **Terminal > New Terminal** and run these commands from the project folder, replacing `<version>` with the version in the JAR filename:

   ```powershell
   javac -cp ".;mysql-connector-j-<version>.jar" DBConnection.java Book.java Main.java
   java -cp ".;mysql-connector-j-<version>.jar" Main
   ```

4. Choose an option from the menu. Use `0` to exit.

If the connection fails, check that MySQL Server is running, the database was created, and the username/password in `DBConnection.java` are correct.

## How issuing and returning work

The `issued` column stores `false` for an available book and `true` for an issued book. Issuing only changes an available book; returning only changes an issued book. The menu reports when the book does not exist or is already in the requested status.
