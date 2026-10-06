# MediCare - Online Healthcare Management System
# Online-Healthcare-Management-System
<img src="assets/icon.png" width="190" height="190">
Our project optimizes healthcare scheduling to streamline appointment management, reduce wait times, and improve patient-care delivery<br>

MediCare is a JavaFX desktop application for managing patients, doctors, appointments, and admin operations.<br>

**Technologies used :** Java, JavaFX, Maven, MySQL

## Database Setup/ pre-requisites :

Before running the application, set up the MySQL database.

1. Install and start **MySQL 8**.

2. Pre-requisites:<br>
   open : `src/main/resources/db.properties` before running and enter your MySQL username and password.<br>

   If you are setting up the project for the first time, you can rename `db.properties.example` to `db.properties`.<br>

3. Run `database/schema.sql`.<br>

   It can run from the MySQL terminal using : `mysql -u root -p < database/schema.sql`<br>

   Or you can open `database/schema.sql` in **MySQL Workbench** and run the script.<br>

   **Warning :** This script is used to setup the database and add demo values, it will delete `healthcare_db` and recreate it so that there are no inconsistancies, it is therefore imperitive that if the database is already set up and working, you do not run it again.<br>

4. To check whether the database connection is working, you can run: `com.healthcare.TestDB`.<br>

## Running the Application/project.

***To use the software in :***

### IntelliJ IDEA **:**

Open the project folder in IntelliJ IDEA and wait for Maven to finish loading.<br>
Then run `Launcher`.<br>


### VS Code **:**

Install the **Extension Pack for Java**, open the project folder, and run `Launcher`.<br>
A `launch.json` file is already included in the project.<br>


### Eclipse **:**

Import the project as an existing Maven project:<br>
`File > Import > Maven > Existing Maven Projects`. Then run `Launcher` as a Java Application.<br>


### Terminal **:**

You can also start the application from the terminal directly using: `mvn javafx:run`.<br>

**Note :** The application should be started using: `com.healthcare.Launcher` **Do not run `Main` directly.**<br>
* This project uses **JDK 23** and **JavaFX 24.0.2**. <br>

## Demo Login Details

| Role    | Email                                                         | Password    |
| ------- | ------------------------------------------------------------- | ----------- |
| Admin   | [admin@healthcare.com](admin@healthcare.com)           | admin123    |
| Doctor  | [anil.verma@healthcare.com](anil.verma@healthcare.com) | password123 |
| Patient | [rahul@gmail.com](rahul@gmail.com)                     | password123 |

<br>

## Project Structure

```
database/
    DatabaseConnection
    schema.sql

dao/
    Database and SQL related operations

service/
    Application logic and business rules

*View.java
    JavaFX screens

*Dashboard.java
    Dashboard screens
```

The database folder contains the tables, view, stored procedure, and demo data.<br> `DatabaseConnection` reads the settings from `db.properties`. The database URL, username, and password can also be provided using the environment variables `DB_URL`, `DB_USER`, and `DB_PASSWORD`. <br>
The `dao` package contains the SQL/database operations, while the `service` package contains the application logic such as settings, schedules, and the doctor dashboard.<br>

###### Team composistion :
|Team composion|Roles             |Name             |Work done                                    |
|--------------|------------------|-----------------|---------------------------------------------|
|Team Leader   |Integration       | Abhinav Jha     | Integration, documentation, leader, testing |
|Member        |Frontend developer| Nitesh Sharma   | frontend development, frontend design       |
|Member        |DBMS developer    | Sameer ED       | DBMS development, DBMS Design               |
|Member        |Backend developer | Rajkumar Rajpoot| backend development                         |
