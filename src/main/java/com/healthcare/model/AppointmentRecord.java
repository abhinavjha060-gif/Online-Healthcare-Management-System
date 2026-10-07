package com.healthcare.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** One appointment row, joined with the doctor's name and speciality. */
public class AppointmentRecord {

    private final int appointmentId;
    private final String doctorName;
    private final String specialization;
    private final LocalDate date;
    private final LocalTime time;
    private final String status;
    private final String reason;
    private final boolean rated;

    public AppointmentRecord(int appointmentId, String doctorName, String specialization,
                             LocalDate date, LocalTime time, String status, String reason) {
        this(appointmentId, doctorName, specialization, date, time, status, reason, false);
    }

    public AppointmentRecord(int appointmentId, String doctorName, String specialization,
                             LocalDate date, LocalTime time, String status, String reason,
                             boolean rated) {
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.date = date;
        this.time = time;
        this.status = status;
        this.reason = reason;
        this.rated = rated;
    }

    public int getAppointmentId()     { return appointmentId; }
    public String getDoctorName()     { return doctorName; }
    public String getSpecialization() { return specialization; }
    public LocalDate getDate()        { return date; }
    public LocalTime getTime()        { return time; }
    public String getStatus()         { return status; }
    public String getReason()         { return reason; }
    public boolean isRated()          { return rated; }

    /** e.g. 02/10/2026 */
    public String getDateDisplay() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH));
    }

    /** e.g. 2 October 2026 */
    public String getLongDate() {
        return date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
    }

    /** e.g. 10:00 AM */
    public String getTimeDisplay() {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH));
    }
}
