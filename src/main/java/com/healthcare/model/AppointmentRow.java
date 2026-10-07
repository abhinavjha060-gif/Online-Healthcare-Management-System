package com.healthcare.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** One appointment as the ADMIN sees it (patient + doctor names). */
public class AppointmentRow {

    private final int appointmentId;
    private final int doctorId;
    private final String patientName;
    private final String doctorName;
    private final LocalDate date;
    private final LocalTime time;
    private final String status;

    public AppointmentRow(int appointmentId, int doctorId, String patientName, String doctorName,
                          LocalDate date, LocalTime time, String status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getAppointmentId() { return appointmentId; }
    public int getDoctorId()      { return doctorId; }
    public String getPatientName(){ return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getStatus()     { return status; }

    public String getDateDisplay() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH));
    }

    public String getTimeDisplay() {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH));
    }
}
