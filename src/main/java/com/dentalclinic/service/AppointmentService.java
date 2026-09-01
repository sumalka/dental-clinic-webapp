package com.dentalclinic.service;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.model.Appointment;
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

    public boolean createAppointment(Appointment appointment) {
        appointment.setAppointmentNumber(appointmentDAO.generateAppointmentNumber());
        appointment.setStatus("SCHEDULED");

        if (appointment.getPatientId() <= 0) {
            return false;
        }
        if (appointment.getDentistId() <= 0) {
            return false;
        }
        if (appointment.getAppointmentDate() == null) {
            return false;
        }

        return appointmentDAO.addAppointment(appointment);
    }

    // FIXED: This method now updates ALL fields of the appointment
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

        return appointmentDAO.updateAppointment(appointment);
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, status);
    }

    public int getTodayAppointmentCount() {
        return appointmentDAO.getTodayAppointmentCount();
    }
}