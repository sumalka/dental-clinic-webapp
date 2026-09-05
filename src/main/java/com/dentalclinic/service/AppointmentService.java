package com.dentalclinic.service;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.getAppointmentById(appointmentId);
    }

    public Appointment getAppointmentByNumber(String appointmentNumber) {
        return appointmentDAO.getAppointmentByNumber(appointmentNumber);
    }

    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentDAO.getAppointmentsByDate(java.time.LocalDate.parse(date));
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        return appointmentDAO.getAppointmentsByPatientId(patientId);
    }

    public boolean isDentistAvailable(int dentistId, LocalDate date,
                                      LocalTime time, int durationMinutes, Integer excludeAppointmentId) {
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDentistAndDate(dentistId, date);

        LocalTime requestedStart = time;
        LocalTime requestedEnd = time.plusMinutes(durationMinutes);

        System.out.println("=========================================");
        System.out.println("🔍 CHECKING DENTIST AVAILABILITY");
        System.out.println("Dentist ID: " + dentistId);
        System.out.println("Date: " + date);
        System.out.println("Requested Time: " + requestedStart + " - " + requestedEnd);
        System.out.println("Duration: " + durationMinutes + " minutes");
        System.out.println("Exclude Appointment ID: " + (excludeAppointmentId != null ? excludeAppointmentId : "None"));
        System.out.println("Total appointments on this date: " + appointments.size());
        System.out.println("-----------------------------------------");

        for (Appointment existing : appointments) {
            if (excludeAppointmentId != null && existing.getAppointmentId() == excludeAppointmentId) {
                System.out.println(" ⏭Skipping self appointment ID: " + existing.getAppointmentId());
                continue;
            }

            if ("CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                System.out.println("⏭Skipping cancelled appointment ID: " + existing.getAppointmentId());
                continue;
            }

            LocalTime existingStart = existing.getAppointmentTime();
            LocalTime existingEnd = existing.getAppointmentTime()
                    .plusMinutes(existing.getDurationMinutes());

            System.out.println("Existing Appointment ID: " + existing.getAppointmentId());
            System.out.println("   Time: " + existingStart + " - " + existingEnd);
            System.out.println("   Status: " + existing.getStatus());

            boolean overlaps = requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);

            if (overlaps) {
                System.out.println("OVERLAP DETECTED!");
                System.out.println("   Requested: " + requestedStart + " - " + requestedEnd);
                System.out.println("   Existing: " + existingStart + " - " + existingEnd);
                System.out.println("=========================================");
                return false;
            } else {
                System.out.println("No overlap with this appointment");
            }
        }

        System.out.println("Dentist IS AVAILABLE at this time");
        System.out.println("=========================================");
        return true;
    }

    public boolean createAppointment(Appointment appointment) {
        LocalDate today = LocalDate.now();
        if (appointment.getAppointmentDate().isBefore(today)) {
            System.out.println("Cannot book appointment in the past: " + appointment.getAppointmentDate());
            return false;
        }

        System.out.println("Creating new appointment...");

        if (!isDentistAvailable(appointment.getDentistId(), appointment.getAppointmentDate(),
                appointment.getAppointmentTime(), appointment.getDurationMinutes(), null)) {
            System.out.println("Dentist" + appointment.getDentistId() + " is not available at that time");
            return false;
        }

        appointment.setStatus("SCHEDULED");

        if (appointment.getPatientId() <= 0) {
            System.out.println("Invalid patient ID: " + appointment.getPatientId());
            return false;
        }
        if (appointment.getDentistId() <= 0) {
            System.out.println("Invalid dentist ID: " + appointment.getDentistId());
            return false;
        }
        if (appointment.getAppointmentDate() == null) {
            System.out.println("Invalid appointment date");
            return false;
        }

        boolean result = appointmentDAO.addAppointment(appointment);
        System.out.println("Appointment creation result: " + (result ? "SUCCESS" : "FAILED"));
        return result;
    }

    public boolean updateAppointment(Appointment appointment) {
        if (appointment.getAppointmentId() <= 0) {
            System.out.println("Invalid appointment ID: " + appointment.getAppointmentId());
            return false;
        }
        if (appointment.getPatientId() <= 0) {
            System.out.println("Invalid patient ID: " + appointment.getPatientId());
            return false;
        }
        if (appointment.getDentistId() <= 0) {
            System.out.println("Invalid dentist ID: " + appointment.getDentistId());
            return false;
        }
        if (appointment.getAppointmentDate() == null) {
            System.out.println("Invalid appointment date");
            return false;
        }

        System.out.println("Updating appointment ID: " + appointment.getAppointmentId());

        if (!isDentistAvailable(appointment.getDentistId(), appointment.getAppointmentDate(),
                appointment.getAppointmentTime(), appointment.getDurationMinutes(),
                appointment.getAppointmentId())) {
            System.out.println("Dentist " + appointment.getDentistId() + " is not available at that time");
            return false;
        }

        boolean result = appointmentDAO.updateAppointment(appointment);
        System.out.println("Appointment update result: " + (result ? "SUCCESS" : "FAILED"));
        return result;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, status);
    }

    public int getTodayAppointmentCount() {
        return appointmentDAO.getTodayAppointmentCount();
    }
}