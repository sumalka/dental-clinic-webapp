package com.dentalclinic.service;

import com.dentalclinic.dao.DentistDAO;
import com.dentalclinic.model.Dentist;
import java.util.List;

public class DentistService {
    private final DentistDAO dentistDAO = new DentistDAO();

    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    public Dentist getDentistById(int dentistId) {
        return dentistDAO.getDentistById(dentistId);
    }

    public boolean addDentist(Dentist dentist) {
        if (dentist.getFirstName() == null || dentist.getFirstName().trim().isEmpty()) {
            System.out.println("First name is required");
            return false;
        }
        if (dentist.getLastName() == null || dentist.getLastName().trim().isEmpty()) {
            System.out.println("Last name is required");
            return false;
        }
        if (dentist.getSpecialization() == null || dentist.getSpecialization().trim().isEmpty()) {
            System.out.println("Specialization is required");
            return false;
        }
        if (dentist.getLicenseNumber() == null || dentist.getLicenseNumber().trim().isEmpty()) {
            System.out.println("License number is required");
            return false;
        }
        if (dentist.getContactNumber() == null || dentist.getContactNumber().trim().isEmpty()) {
            System.out.println("Contact number is required");
            return false;
        }
        if (dentist.getEmail() == null || dentist.getEmail().trim().isEmpty()) {
            System.out.println("Email is required");
            return false;
        }
        if (dentist.getConsultationFee() <= 0) {
            System.out.println("Consultation fee must be greater than 0");
            return false;
        }

        dentist.setActive(true);
        boolean result = dentistDAO.addDentist(dentist);
        System.out.println("Add dentist result: " + result);
        return result;
    }

    public boolean updateDentist(Dentist dentist) {
        if (dentist.getDentistId() <= 0) {
            System.out.println("Invalid dentist ID: " + dentist.getDentistId());
            return false;
        }
        if (dentist.getFirstName() == null || dentist.getFirstName().trim().isEmpty()) {
            System.out.println("First name is required");
            return false;
        }
        if (dentist.getLastName() == null || dentist.getLastName().trim().isEmpty()) {
            System.out.println("Last name is required");
            return false;
        }
        if (dentist.getSpecialization() == null || dentist.getSpecialization().trim().isEmpty()) {
            System.out.println("Specialization is required");
            return false;
        }
        if (dentist.getLicenseNumber() == null || dentist.getLicenseNumber().trim().isEmpty()) {
            System.out.println("License number is required");
            return false;
        }
        if (dentist.getContactNumber() == null || dentist.getContactNumber().trim().isEmpty()) {
            System.out.println("Contact number is required");
            return false;
        }
        if (dentist.getEmail() == null || dentist.getEmail().trim().isEmpty()) {
            System.out.println("Email is required");
            return false;
        }
        if (dentist.getConsultationFee() <= 0) {
            System.out.println("Consultation fee must be greater than 0");
            return false;
        }

        // DEBUG: Print the status being updated
        System.out.println("=== UPDATING DENTIST IN SERVICE ===");
        System.out.println("ID: " + dentist.getDentistId());
        System.out.println("Name: " + dentist.getFirstName() + " " + dentist.getLastName());
        System.out.println("Active status being set to: " + dentist.isActive());
        System.out.println("====================================");

        boolean result = dentistDAO.updateDentist(dentist);
        System.out.println("Update dentist result: " + result);
        return result;
    }

    public boolean deleteDentist(int dentistId) {
        if (dentistId <= 0) {
            System.out.println("Invalid dentist ID: " + dentistId);
            return false;
        }
        boolean result = dentistDAO.deleteDentist(dentistId);
        System.out.println("Delete dentist result: " + result);
        return result;
    }
}