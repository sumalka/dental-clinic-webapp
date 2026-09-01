package com.dentalclinic.model;

public class Dentist {
    private int dentistId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String licenseNumber;
    private String contactNumber;
    private String email;
    private double consultationFee;
    private String workingDays;
    private String workingHours;
    private boolean active;  // Changed from isActive to active

    public Dentist() {}

    public Dentist(String firstName, String lastName, String specialization,
                   String licenseNumber, String contactNumber, String email, double consultationFee) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.contactNumber = contactNumber;
        this.email = email;
        this.consultationFee = consultationFee;
        this.active = true;
    }

    // Getters and Setters
    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return "Dr. " + firstName + " " + lastName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getWorkingDays() { return workingDays; }
    public void setWorkingDays(String workingDays) { this.workingDays = workingDays; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}