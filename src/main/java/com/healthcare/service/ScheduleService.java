package com.healthcare.service;

import com.healthcare.dao.ScheduleDAO;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Rules for the doctor's weekly schedule: turns the drop-down texts
 * ("09:00 AM") into times, validates them and talks to the DAO.
 */
public class ScheduleService {

    private static final DateTimeFormatter UI_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    private final ScheduleDAO dao = new ScheduleDAO();

    /**
     * @param available false = the doctor does not work that day
     * @return null when saved, otherwise a message to show to the user
     */
    public String save(int doctorId, String day, String startText, String endText,
                       boolean available) throws SQLException {

        if (!available) {
            dao.clearDay(doctorId, day);
            return null;
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startText, UI_FORMAT);
            end = LocalTime.parse(endText, UI_FORMAT);
        } catch (DateTimeParseException e) {
            return "Please choose valid start and end times.";
        }

        if (!start.isBefore(end)) {
            return "End time must be after the start time.";
        }

        dao.saveDay(doctorId, day, start, end);
        return null;
    }

    public List<ScheduleDAO.Slot> weeklySchedule(int doctorId) throws SQLException {
        return dao.getForDoctor(doctorId);
    }

    public static String format(LocalTime time) {
        return time.format(UI_FORMAT);
    }
}
