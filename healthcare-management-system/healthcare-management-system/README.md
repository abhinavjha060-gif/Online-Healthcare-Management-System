# Online Healthcare Management System

A 3rd-semester live project: a Spring Boot web app where **Patients** book appointments,
**Doctors** manage their schedule and add diagnoses/prescriptions, and **Admins** manage
users and oversee the whole system.

Built with: Java 17 (compiles fine with your JDK 25), Spring Boot 3.3.4, Spring MVC,
Spring Security, Spring Data JPA, Thymeleaf, H2 (default) / MySQL (optional, for hosting online).


## 1. Run it locally (for your demo/grading)

**Option A - from Eclipse**
1. `File → Import → Maven → Existing Maven Projects` → select this folder.
2. Wait for Eclipse to download dependencies (needs internet the first time).
3. Right-click `HmsApplication.java` → `Run As → Java Application`.
4. Open **http://localhost:8080** in your browser.

**Option B - from a terminal**
```bash
mvn spring-boot:run
```
or build a runnable jar and run it like a normal app:
```bash
mvn clean package
java -jar target/healthcare-management-system.jar
```

No database setup needed — it uses a file-based H2 database (`./data/hms.mv.db`), created
automatically the first time you run it. That's what makes this behave like a normal
double-click-and-run app.<br>

**Option C - Running it in IntelliJ IDEA**
1. File → Open, then pick the project folder (the one containing pom.xml) and click "Trust Project".
	Wait for the progress bar at the bottom right to finish (Maven import).
	If it complains about the SDK, go to File → Project Structure → Project → SDK and choose a JDK 17 or newer.
2. Open HmsApplication.java and click the green ▶ next to main, then Run.
3. Open http://localhost:8080.
4. To stop, click the red ■ in the Run panel.
**Option D -Running it in VS Code**
1. Install the Extension Pack for Java (from the Extensions tab).
2. File → Open Folder, then pick the project folder. Wait for the "Java projects" loading indicator at the bottom to finish.
3. Open HmsApplication.java and click Run above the main method.
4. Open http://localhost:8080.
5. To stop, click the red ■ in the debug toolbar, or press Ctrl+C if it's running in the terminal.

### Demo login credentials (auto-created on first run)

| Role    | Email              | Password  |
|---------|--------------------|-----------|
| Admin   | admin@hms.com      | admin123  |
| Doctor  | doctor@hms.com     | doctor123 |
| Patient | patient@hms.com    | patient123|

New patients can also self-register at `/register`. Doctor and Admin accounts are
created only by an Admin (via **Add Doctor** in the admin dashboard) — this mirrors
how a real hospital system would work.

---

## 2. What it does (feature summary)

**Admin**
- Dashboard with user/doctor/patient/appointment counts
- View users and delete any user except the account you're logged in with
  (deleting a doctor/patient also deletes their appointments and medical records)
- Create new Doctor accounts
- View every appointment in the system and delete cancelled ones

**Doctor**
- Dashboard with appointment stats
- Confirm / cancel appointments, and delete cancelled ones
- Add a diagnosis + prescription record for a confirmed appointment (auto-marks it Completed)
- View all medical records they've created

**Patient**
- Self-register
- Book an appointment with any doctor (date/time + reason)
- View their appointment history and status
- Cancel a pending/confirmed appointment, and delete cancelled ones
- View their medical records (diagnosis/prescription history)

**Any user (My Account page)**
- Patients and doctors can delete their own account (password required).
  This also deletes their appointments and medical records. Admin accounts can't
  self-delete, so the system always has at least one admin.

---

## 5. Project structure

```
src/main/java/com/hms/
  HmsApplication.java        - entry point
  config/                    - Spring Security config, demo data seeder
  model/                     - JPA entities (User, DoctorProfile, PatientProfile,
                                Appointment, MedicalRecord) + enums
  repository/                - Spring Data JPA repositories
  service/                   - business logic (auth, registration, appointments, records)
  controller/                - MVC controllers (Home/Auth, Admin, Doctor, Patient)

src/main/resources/
  application.properties       - default config (H2, works out of the box)
  application-prod.properties  - MySQL config for online deployment
  templates/                   - Thymeleaf HTML pages, organized by role
  static/css/style.css         - all styling (no external CDN dependency)
```

---

## 6. Useful extras

- **H2 console** (peek at the actual database while running locally):
  `http://localhost:8080/h2-console`
  JDBC URL: `jdbc:h2:file:./data/hms`, user `sa`, blank password.
- Passwords are hashed with BCrypt — never stored in plain text.
- Role-based access is enforced both in Spring Security (URL-level) and in the
  Thymeleaf nav bar (`sec:authorize`), so each role only sees relevant links.
