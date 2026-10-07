DROP DATABASE IF EXISTS healthcare_db;
CREATE DATABASE healthcare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE healthcare_db;

-- ---------------------------------------------------------------------
-- users: everybody who can log in (Patient / Doctor / Admin)
-- Passwords are stored as SHA-256 hex 
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,
    phone         VARCHAR(20),
    role          ENUM('Patient','Doctor','Admin') NOT NULL,
    status        ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE patients (
    patient_id      INT PRIMARY KEY,
    date_of_birth   DATE,
    gender          VARCHAR(10),
    blood_group     VARCHAR(5),
    address         VARCHAR(255),
    emergency_phone VARCHAR(20),
    CONSTRAINT fk_patient_user FOREIGN KEY (patient_id)
        REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE doctors (
    doctor_id      INT PRIMARY KEY,
    specialization VARCHAR(100) NOT NULL,
    license_no     VARCHAR(50)  NOT NULL UNIQUE,
    CONSTRAINT fk_doctor_user FOREIGN KEY (doctor_id)
        REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- weekly working hours of a doctor (one row = one working block on one day)
CREATE TABLE doctor_schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id   INT NOT NULL,
    day_of_week ENUM('Monday','Tuesday','Wednesday','Thursday',
                     'Friday','Saturday','Sunday') NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    CONSTRAINT fk_sched_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    CONSTRAINT chk_sched_time CHECK (start_time < end_time)
) ENGINE=InnoDB;

CREATE TABLE appointments (
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT NOT NULL,
    doctor_id        INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason           VARCHAR(255),
    status           ENUM('Pending','Confirmed','Completed','Cancelled')
                     NOT NULL DEFAULT 'Pending',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appt_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    INDEX idx_appt_doctor_date (doctor_id, appointment_date),
    INDEX idx_appt_patient (patient_id)
) ENGINE=InnoDB;

CREATE TABLE medical_records (
    record_id      INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT NOT NULL,
    doctor_id      INT NOT NULL,
    appointment_id INT NULL,
    diagnosis      VARCHAR(255) NOT NULL,
    prescription   TEXT,
    notes          TEXT,
    record_date    DATE NOT NULL,
    CONSTRAINT fk_rec_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_rec_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    CONSTRAINT fk_rec_appt FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE feedback (
    feedback_id    INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE,          -- one review per visit
    patient_id     INT NOT NULL,
    doctor_id      INT NOT NULL,
    rating         TINYINT NOT NULL,
    comments       VARCHAR(500),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_fb_appt FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id) ON DELETE CASCADE,
    CONSTRAINT fk_fb_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_fb_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- key/value settings edited on the Admin "System Settings" screen
CREATE TABLE system_settings (
    setting_key   VARCHAR(60)  PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- health notes the patient edits on "My Health Profile"
-- (conditions, allergies, medicines, vaccinations ...), one row per note
CREATE TABLE patient_health_info (
    patient_id  INT         NOT NULL,
    field_key   VARCHAR(40) NOT NULL,
    field_value TEXT,
    PRIMARY KEY (patient_id, field_key),
    CONSTRAINT fk_health_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- View used by the Admin "Performance Analytics" screen
-- ---------------------------------------------------------------------
CREATE VIEW v_doctor_performance AS
SELECT  u.full_name AS doctor_name,
        d.specialization,
        COUNT(a.appointment_id)                         AS total_appointments,
        COALESCE(SUM(a.status = 'Completed'), 0)        AS completed,
        (SELECT AVG(f.rating) FROM feedback f
          WHERE f.doctor_id = d.doctor_id)              AS avg_rating
FROM doctors d
JOIN users u ON u.user_id = d.doctor_id
LEFT JOIN appointments a ON a.doctor_id = d.doctor_id
GROUP BY d.doctor_id, u.full_name, d.specialization;

-- ---------------------------------------------------------------------
-- Stored procedure used by AppointmentDAO.book()
-- Locks the slot inside a transaction so two patients cannot take it.
-- p_result = 'BOOKED'  or  'SLOT ALREADY BOOKED'
-- ---------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE book_appointment(
    IN  p_patient_id INT,
    IN  p_doctor_id  INT,
    IN  p_date       DATE,
    IN  p_time       TIME,
    IN  p_reason     VARCHAR(255),
    OUT p_result     VARCHAR(60)
)
BEGIN
    DECLARE v_taken INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 'ERROR';
    END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_taken
    FROM appointments
    WHERE doctor_id = p_doctor_id
      AND appointment_date = p_date
      AND appointment_time = p_time
      AND status IN ('Pending','Confirmed')
    FOR UPDATE;

    IF v_taken > 0 THEN
        ROLLBACK;
        SET p_result = 'SLOT ALREADY BOOKED';
    ELSE
        INSERT INTO appointments
            (patient_id, doctor_id, appointment_date, appointment_time, reason, status)
        VALUES
            (p_patient_id, p_doctor_id, p_date, p_time, p_reason, 'Pending');
        COMMIT;
        SET p_result = 'BOOKED';
    END IF;
END //
DELIMITER ;

-- ---------------------------------------------------------------------
-- Demo data   (all demo accounts use the password:  password123
--              except the admin which uses:          admin123)
-- ---------------------------------------------------------------------
INSERT INTO users (full_name, email, password_hash, phone, role) VALUES
 ('System Admin',     'admin@healthcare.com',        SHA2('admin123',256),    '9000000000', 'Admin'),
 ('Dr. Anil Verma',   'anil.verma@healthcare.com',   SHA2('password123',256), '9000000001', 'Doctor'),
 ('Dr. Sneha Sharma', 'sneha.sharma@healthcare.com', SHA2('password123',256), '9000000002', 'Doctor'),
 ('Dr. Rohan Mehta',  'rohan.mehta@healthcare.com',  SHA2('password123',256), '9000000003', 'Doctor'),
 ('Rahul Sharma',     'rahul@gmail.com',             SHA2('password123',256), '9876543210', 'Patient'),
 ('Priya Singh',      'priya@gmail.com',             SHA2('password123',256), '9876543211', 'Patient'),
 ('Aman Kumar',       'aman@gmail.com',              SHA2('password123',256), '9876543212', 'Patient');

INSERT INTO doctors (doctor_id, specialization, license_no) VALUES
 (2, 'General Physician', 'LIC-2'),
 (3, 'Dermatologist',     'LIC-3'),
 (4, 'Cardiologist',      'LIC-4');

INSERT INTO patients (patient_id, date_of_birth, gender, blood_group, address, emergency_phone) VALUES
 (5, '2003-04-12', 'Male',   'B+', 'Delhi, India',   '9876501234'),
 (6, '1998-09-23', 'Female', 'O+', 'Noida, India',   '9876501235'),
 (7, '1990-01-05', 'Male',   'A+', 'Lucknow, India', '9876501236');

-- Monday-Saturday, 09:00-17:00  (a doctor can change this on the Schedule screen)
INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time)
SELECT d.doctor_id, w.day_name, '09:00:00', '17:00:00'
FROM doctors d
JOIN (SELECT 'Monday' AS day_name UNION SELECT 'Tuesday' UNION SELECT 'Wednesday'
      UNION SELECT 'Thursday' UNION SELECT 'Friday' UNION SELECT 'Saturday') w;

INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) VALUES
 (5, 2, CURDATE() - INTERVAL 14 DAY, '10:00:00', 'Fever and cold',    'Completed'),
 (5, 2, CURDATE(),                   '11:00:00', 'Follow-up',         'Confirmed'),
 (6, 2, CURDATE(),                   '10:00:00', 'Skin allergy',      'Pending'),
 (7, 2, CURDATE() + INTERVAL 1 DAY,  '09:00:00', 'Routine check-up',  'Pending'),
 (6, 3, CURDATE() + INTERVAL 2 DAY,  '10:00:00', 'Skin consultation', 'Confirmed');

INSERT INTO medical_records (patient_id, doctor_id, appointment_id, diagnosis, prescription, notes, record_date) VALUES
 (5, 2, 1, 'Viral fever', 'Paracetamol 500mg - twice a day for 3 days', 'Rest and plenty of fluids',
  CURDATE() - INTERVAL 14 DAY);

INSERT INTO feedback (appointment_id, patient_id, doctor_id, rating, comments) VALUES
 (1, 5, 2, 5, 'Very helpful and patient doctor.');

INSERT INTO system_settings (setting_key, setting_value) VALUES
 ('system_name',          'Online Healthcare Management System'),
 ('support_email',        'support@healthcare.com'),
 ('timezone',             'India Standard Time (IST)'),
 ('booking_enabled',      'true'),
 ('cancellation_enabled', 'true'),
 ('slot_duration',        '30 minutes'),
 ('email_notifications',  'true'),
 ('appointment_alerts',   'true');
