package com.dentalclinic.service;

import com.dentalclinic.dao.StaffDAO;
import com.dentalclinic.model.Staff;
import java.util.List;

public class StaffService {
    private final StaffDAO staffDAO = new StaffDAO();

    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    public Staff getStaffById(int staffId) {
        return staffDAO.getStaffById(staffId);
    }

    public Staff getStaffByUsername(String username) {
        return staffDAO.getStaffByUsername(username);
    }

    public Staff authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return staffDAO.authenticate(username, password);
    }

    public boolean addStaff(Staff staff) {
        // Validation
        if (staff.getFirstName() == null || staff.getFirstName().trim().isEmpty()) {
            System.out.println("First name is required");
            return false;
        }
        if (staff.getLastName() == null || staff.getLastName().trim().isEmpty()) {
            System.out.println("Last name is required");
            return false;
        }
        if (staff.getUsername() == null || staff.getUsername().trim().isEmpty()) {
            System.out.println("Username is required");
            return false;
        }
        if (staff.getPassword() == null || staff.getPassword().trim().isEmpty()) {
            System.out.println("Password is required");
            return false;
        }
        if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
            System.out.println("Email is required");
            return false;
        }

        // Check if username already exists
        Staff existing = staffDAO.getStaffByUsername(staff.getUsername());
        if (existing != null) {
            System.out.println("Username already exists: " + staff.getUsername());
            return false;
        }

        staff.setStatus("Active");
        return staffDAO.addStaff(staff);
    }

    public boolean updateStaff(Staff staff) {
        if (staff.getStaffId() <= 0) {
            System.out.println("Invalid staff ID");
            return false;
        }
        return staffDAO.updateStaff(staff);
    }

    public boolean deleteStaff(int staffId) {
        if (staffId <= 0) {
            System.out.println("Invalid staff ID");
            return false;
        }
        return staffDAO.deleteStaff(staffId);
    }

    public boolean toggleStaffStatus(int staffId) {
        Staff staff = staffDAO.getStaffById(staffId);
        if (staff == null) {
            return false;
        }
        String newStatus = staff.getStatus().equals("Active") ? "Inactive" : "Active";
        return staffDAO.updateStaffStatus(staffId, newStatus);
    }

    /**
     * Update staff password by staff ID
     * @param staffId The staff ID
     * @param newPassword The new password
     * @return true if updated successfully, false otherwise
     */
    public boolean updateStaffPassword(int staffId, String newPassword) {
        if (staffId <= 0) {
            System.out.println("Invalid staff ID: " + staffId);
            return false;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            System.out.println("Password cannot be empty");
            return false;
        }

        Staff staff = staffDAO.getStaffById(staffId);
        if (staff == null) {
            System.out.println("Staff not found with ID: " + staffId);
            return false;
        }

        staff.setPassword(newPassword);
        return staffDAO.updateStaff(staff);
    }
}