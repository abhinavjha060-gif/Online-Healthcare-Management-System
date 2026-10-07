package com.healthcare.service;

import com.healthcare.dao.DoctorAppointmentDAO;
import com.healthcare.dao.DoctorDAO;
import com.healthcare.model.PatientAppointment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds everything the doctor's home screen shows from the doctor's appointments.
 * One database call, all the counting is done here (business logic, not UI).
 */
public class DoctorDashboardService {

    /** Ready-to-display numbers and lists. */
    public static class Summary {
        public String specialization = "";
        public int totalPatients;
        public int newPatientsThisMonth;
        public int todayTotal;
        public int todayPending;
        public int pendingAll;
        public int completedToday;
        public int confirmedWaitingToday;
        public int tomorrowCount;
        public boolean workingToday;                                  // has working hours today
        public List<PatientAppointment> today = new ArrayList<>();   // not cancelled, by time
        public List<PatientAppointment> queue = new ArrayList<>();   // today, Confirmed, still waiting
    }

    public Summary load(int doctorId) throws SQLException {

        Summary s = new Summary();
        s.specialization = new DoctorDAO().getSpecialization(doctorId);

        List<PatientAppointment> all = new DoctorAppointmentDAO().getForDoctor(doctorId);

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate tomorrow = today.plusDays(1);

        // total patients = different patients who ever booked this doctor;
        // new this month = patients whose FIRST booking is in this month
        Map<Integer, LocalDate> firstVisit = new HashMap<>();
        Set<Integer> patients = new HashSet<>();

        for (PatientAppointment a : all) {
            if ("Cancelled".equals(a.getStatus())) {
                continue;
            }
            patients.add(a.getPatientId());
            firstVisit.merge(a.getPatientId(), a.getDate(),
                    (x, y) -> x.isBefore(y) ? x : y);

            if ("Pending".equals(a.getStatus())) {
                s.pendingAll++;
            }
            if (a.getDate().equals(tomorrow)
                    && ("Pending".equals(a.getStatus()) || "Confirmed".equals(a.getStatus()))) {
                s.tomorrowCount++;
            }
            if (a.getDate().equals(today)) {
                s.todayTotal++;
                s.today.add(a);

                switch (a.getStatus()) {
                    case "Pending"   -> s.todayPending++;
                    case "Completed" -> s.completedToday++;
                    case "Confirmed" -> {
                        s.confirmedWaitingToday++;
                        s.queue.add(a);
                    }
                    default -> { }
                }
            }
        }

        String todayName = today.getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        s.workingToday = new com.healthcare.dao.ScheduleDAO().getForDoctor(doctorId).stream()
                .anyMatch(slot -> slot.day.equalsIgnoreCase(todayName));

        s.totalPatients = patients.size();
        s.newPatientsThisMonth = (int) firstVisit.values().stream()
                .filter(d -> !d.isBefore(monthStart)).count();

        s.today = s.today.stream()
                .sorted((x, y) -> x.getTime().compareTo(y.getTime()))
                .collect(Collectors.toList());
        s.queue = s.queue.stream()
                .sorted((x, y) -> x.getTime().compareTo(y.getTime()))
                .collect(Collectors.toList());
        return s;
    }
}
