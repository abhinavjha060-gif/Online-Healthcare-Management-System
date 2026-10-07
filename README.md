# MediCare - Online Healthcare Management System
# Online-Healthcare-Management-System
<img src="assets/icon.png" width="190" height="190">
Our project optimizes healthcare scheduling to streamline appointment management, reduce wait times, and improve patient-care delivery<br>

MediCare is a JavaFX desktop application for managing patients, doctors, appointments and admin operations. Each user type (Admin, Doctor, Patient) has its own dashboard.

**Technologies used:** Java 23, JavaFX 24.0.2, Maven, MySQL 8 (JDBC)

## Features

| Role    | What they can do |
| ------- | ---------------- |
| Admin   | **User Management** - add, edit, activate/deactivate and delete users (Patient / Doctor / Admin).<br>**Appointment Management** - view all appointments; schedule, reschedule, confirm, cancel and delete.<br>**System Settings** - system name, support e-mail, time zone, slot duration, turn booking / cancellation on or off, notification options.<br>**Performance Analytics** - totals, appointment status graph and doctor performance (appointments, completed, average rating). |
| Doctor  | **Schedule Management** - weekly working hours, plus a calendar of booked appointments.<br>**Patient Records** - view patient history, add and update medical records.<br>**Appointments** - confirm, cancel, complete a visit (saves diagnosis and prescription).<br>**Patient Feedback** - ratings and comments from patients. |
| Patient | **Registration / Login / Forgot password**.<br>**Book Appointment** - free slots are generated from the doctor's working hours and the admin's slot duration.<br>**My Appointments** - history with status, cancel, rate a completed visit.<br>**Medical History** - records written by doctors (diagnosis, prescription, notes).<br>**My Profile** - personal details, emergency contact, health notes (conditions, allergies, medicines, vaccinations ...) and change password. |

## Database Setup / Pre-requisites

Before running the application, set up the MySQL database.

1. Install and start **MySQL 8**.

2. Run `database/schema.sql`.<br>
   From the MySQL terminal: `source database/schema.sql;`<br>

   Or open `database/schema.sql` in **MySQL Workbench** and run the script.<br>

   if you are in command line terminal: use `mysql -u root -p < database/schema.sql` when in the same directory. <br>


   **Warning:** this script creates the database and adds demo data. It **deletes `healthcare_db` and recreates it** (*if it exists already*), so do not run it again if the database is already set up and working.

3. Enter your MySQL username and password.<br>
   First time only: copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties`. Then open `db.properties` and set `db.user` and `db.password`.<br>
   **note: *the db.properties.example and db.properties both have the same placeholder. hence why you can add you username/password directly to db.properties***<br>
   (`db.properties` is listed in `.gitignore`, so your password is not committed.)<br>

4. To check that the connection works, run `com.healthcare.TestDB`.

## Running the Application

**Requirements:** JDK 23 and Maven (JavaFX 24.0.2 is downloaded by Maven).

Always start the application with `com.healthcare.Launcher`. **Do not run `Main` directly** - it fails with a "JavaFX runtime components are missing" error in most IDEs.

### IntelliJ IDEA
Open the project folder and wait for Maven to finish loading. Then run `Launcher`.

### VS Code
Install the **Extension Pack for Java**, open the project folder and run `Launcher`. A `launch.json` file is already included.

### Eclipse
`File > Import > Maven > Existing Maven Projects`. Then run `Launcher` as a Java Application.

### Terminal
`mvn javafx:run` (the Maven plugin is already configured to start `Launcher`).

## Demo Login Details

| Role    | Email                     | Password    |
| ------- | ------------------------- | ----------- |
| Admin   | admin@healthcare.com      | admin123    |
| Doctor  | anil.verma@healthcare.com | password123 |
| Patient | rahul@gmail.com           | password123 |

Other demo doctors: `sneha.sharma@healthcare.com`, `rohan.mehta@healthcare.com`. Other demo patients: `priya@gmail.com`, `aman@gmail.com` (all use `password123`).

Users created by the admin get the temporary password `password123`; patients can change it from My Profile. ("Forgot password" needs the phone number given at registration, so it only works for users who have one.) A doctor added by the admin has no working hours yet, so the doctor must first set a schedule on the Schedule screen before patients can book.

## Project Structure

```
database/
    schema.sql                 tables, view, stored procedure, demo data

src/main/java/com/healthcare/
    Launcher.java, Main.java   start the app (use Launcher)
    TestDB.java                database connection check
    *View.java                 JavaFX screens
    *Dashboard.java            dashboards (Admin, Doctor, Patient)
    database/                  DatabaseConnection
    dao/                       SQL / database operations
    service/                   application logic and business rules
    model/                     data classes (User, UserSession, rows ...)

src/main/resources/
    db.properties(.example)    database settings
    styles.css
```

`DatabaseConnection` reads the settings from `db.properties`. The URL, username and password can also be given with the environment variables `DB_URL`, `DB_USER` and `DB_PASSWORD`.<br>

The `dao` package holds the SQL, while the `service` package holds logic such as settings, schedules and the doctor dashboard.<br>

The tables `system_settings` and `patient_health_info` are also created automatically by the app if they are missing.

## Team Composition

| Role        | Responsibility     | Name              |Workdone                                         |
|-------------|--------------------|-------------------|-------------------------------------------------|
| Team Leader | Integration        | Abhinav Jha       | Integration, documentation, leadership, testing |
| Member      | Frontend developer | Nitesh Sharma     | Frontend development, frontend design           |
| Member      | DBMS developer     | Sameer ED         | DBMS development, DBMS design                   |
| Member      | Backend developer  | Rajkumar Rajpoot  | Backend development                             |
