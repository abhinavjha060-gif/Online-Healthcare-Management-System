package com.healthcare.model;

/** One doctor, as shown in the "Select Doctor" drop-down. */
public class Doctor {

    private final int doctorId;
    private final String name;
    private final String specialization;

    public Doctor(int doctorId, String name, String specialization) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
    }

    public int getDoctorId()          { return doctorId; }
    public String getName()           { return name; }
    public String getSpecialization() { return specialization; }

    /** Text shown in the drop-down, e.g. "Dr. Anil Verma - General Physician". */
    public String getLabel() {
        return name + " - " + specialization;
    }
}
