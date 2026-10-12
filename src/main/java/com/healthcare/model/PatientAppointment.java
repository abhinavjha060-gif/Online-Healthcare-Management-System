package com.healthcare.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** One appointment as the DOCTOR sees it (with the patient's name). */
public class PatientAppointment {

    private final int appointmentId;
    private final int patientId;
    private final String patientName;
    private final LocalDate date;
    private final LocalTime time;
    private final String reason;
    private final String status;

    public PatientAppointment(int appointmentId, int patientId, String patientName,
                              LocalDate date, LocalTime time, String reason, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

    public int getAppointmentId() { return appointmentId; }
    public int getPatientId()     { return patientId; }
    public String getPatientName(){ return patientName; }
    public LocalDate getDate()    { return date; }
    public LocalTime getTime()    { return time; }
    public String getReason()     { return reason == null ? "" : reason; }
    public String getStatus()     { return status; }

    public String getDateDisplay() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH));
    }

    public String getTimeDisplay() {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH));
    }
}
