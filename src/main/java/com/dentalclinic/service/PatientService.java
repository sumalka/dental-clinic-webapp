package com.dentalclinic.service;

import com.dentalclinic.dao.PatientDAO;
import com.dentalclinic.model.Patient;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.logging.Logger;

public class PatientService {
    private static final Logger LOGGER = Logger.getLogger(PatientService.class.getName());
    private final PatientDAO patientDAO = new PatientDAO();

    // ============= GET ALL PATIENTS =============
    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    // ============= GET PATIENT BY ID =============
    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    // ============= SEARCH PATIENTS =============
    public List<Patient> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return patientDAO.getAllPatients();
        }
        return patientDAO.searchPatients(searchTerm.trim());
    }

    // ============= ADD PATIENT =============
    public boolean addPatient(Patient patient) {
        // Validation
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            LOGGER.warning("First name is required");
            return false;
        }
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            LOGGER.warning("Last name is required");
            return false;
        }
        if (patient.getContactNumber() == null || patient.getContactNumber().trim().isEmpty()) {
            LOGGER.warning("Contact number is required");
            return false;
        }
        if (patient.getDateOfBirth() == null) {
            LOGGER.warning("Date of birth is required");
            return false;
        }
        if (patient.getAddress() == null || patient.getAddress().trim().isEmpty()) {
            LOGGER.warning("Address is required");
            return false;
        }
        if (patient.getCity() == null || patient.getCity().trim().isEmpty()) {
            LOGGER.warning("City is required");
            return false;
        }
        if (patient.getIdNumber() == null || patient.getIdNumber().trim().isEmpty()) {
            LOGGER.warning("ID number is required");
            return false;
        }

        // Set default status if not set
        if (patient.getStatus() == null) {
            patient.setStatus("Active");
        }

        return patientDAO.addPatient(patient);
    }

    // ============= UPDATE PATIENT =============
    public boolean updatePatient(Patient patient) {
        if (patient.getPatientId() <= 0) {
            LOGGER.warning("Invalid patient ID");
            return false;
        }
        return patientDAO.updatePatient(patient);
    }

    // ============= DELETE PATIENT =============
    public boolean deletePatient(int patientId) {
        if (patientId <= 0) {
            LOGGER.warning("Invalid patient ID");
            return false;
        }
        return patientDAO.deletePatient(patientId);
    }

    // ============= GET STATISTICS =============
    public JsonObject getPatientStats() {
        JsonObject stats = new JsonObject();
        stats.addProperty("totalPatients", patientDAO.getTotalPatientCount());
        stats.addProperty("activePatients", patientDAO.getActivePatientCount());
        stats.addProperty("newThisMonth", patientDAO.getNewThisMonthCount());
        return stats;
    }

    // ============= GET TOTAL PATIENT COUNT =============
    public int getTotalPatientCount() {
        return patientDAO.getTotalPatientCount();
    }
}