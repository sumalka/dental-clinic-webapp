package com.dentalclinic.dao;

import com.dentalclinic.model.Dentist;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    public List<Dentist> getAllDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY first_name, last_name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dentists.add(extractDentistFromResultSet(rs));
            }
            System.out.println("Loaded " + dentists.size() + " dentists from database");
            // Debug: Print each dentist's status
            for (Dentist d : dentists) {
                System.out.println("  - ID: " + d.getDentistId() + ", Name: " + d.getFullName() + ", Active: " + d.isActive());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dentists;
    }

    public Dentist getDentistById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Dentist dentist = extractDentistFromResultSet(rs);
                System.out.println("Retrieved dentist: " + dentist.getFullName() + ", Active: " + dentist.isActive());
                return dentist;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDentist(Dentist dentist) {
        String sql = "INSERT INTO dentists (first_name, last_name, specialization, license_number, " +
                "contact_number, email, consultation_fee, working_days, working_hours, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, dentist.getFirstName());
            stmt.setString(2, dentist.getLastName());
            stmt.setString(3, dentist.getSpecialization());
            stmt.setString(4, dentist.getLicenseNumber());
            stmt.setString(5, dentist.getContactNumber());
            stmt.setString(6, dentist.getEmail());
            stmt.setDouble(7, dentist.getConsultationFee());
            stmt.setString(8, dentist.getWorkingDays());
            stmt.setString(9, dentist.getWorkingHours());
            stmt.setBoolean(10, dentist.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    dentist.setDentistId(rs.getInt(1));
                }
                System.out.println("Added dentist: " + dentist.getFullName() + ", Active: " + dentist.isActive());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDentist(Dentist dentist) {
        String sql = "UPDATE dentists SET first_name = ?, last_name = ?, specialization = ?, " +
                "license_number = ?, contact_number = ?, email = ?, consultation_fee = ?, " +
                "working_days = ?, working_hours = ?, is_active = ? WHERE dentist_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // DEBUG: Print all values being updated
            System.out.println("=== EXECUTING UPDATE IN DAO ===");
            System.out.println("ID: " + dentist.getDentistId());
            System.out.println("First Name: " + dentist.getFirstName());
            System.out.println("Last Name: " + dentist.getLastName());
            System.out.println("Specialization: " + dentist.getSpecialization());
            System.out.println("License: " + dentist.getLicenseNumber());
            System.out.println("Contact: " + dentist.getContactNumber());
            System.out.println("Email: " + dentist.getEmail());
            System.out.println("Fee: " + dentist.getConsultationFee());
            System.out.println("Working Days: " + dentist.getWorkingDays());
            System.out.println("Working Hours: " + dentist.getWorkingHours());
            System.out.println("is_active (boolean): " + dentist.isActive());
            System.out.println("is_active (int for DB): " + (dentist.isActive() ? 1 : 0));
            System.out.println("================================");

            stmt.setString(1, dentist.getFirstName());
            stmt.setString(2, dentist.getLastName());
            stmt.setString(3, dentist.getSpecialization());
            stmt.setString(4, dentist.getLicenseNumber());
            stmt.setString(5, dentist.getContactNumber());
            stmt.setString(6, dentist.getEmail());
            stmt.setDouble(7, dentist.getConsultationFee());
            stmt.setString(8, dentist.getWorkingDays());
            stmt.setString(9, dentist.getWorkingHours());
            stmt.setBoolean(10, dentist.isActive());
            stmt.setInt(11, dentist.getDentistId());

            int rows = stmt.executeUpdate();
            System.out.println("Updated dentist ID " + dentist.getDentistId() +
                    ", Active: " + dentist.isActive() + ", rows affected: " + rows);

            // Verify the update by reading back
            if (rows > 0) {
                Dentist updated = getDentistById(dentist.getDentistId());
                if (updated != null) {
                    System.out.println("VERIFICATION - Retrieved active status: " + updated.isActive());
                    System.out.println("VERIFICATION - Status matches: " + (updated.isActive() == dentist.isActive()));
                }
            }

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDentist(int dentistId) {
        String sql = "DELETE FROM dentists WHERE dentist_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            int rows = stmt.executeUpdate();
            System.out.println("Deleted dentist ID " + dentistId + ", rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Dentist extractDentistFromResultSet(ResultSet rs) throws SQLException {
        Dentist dentist = new Dentist();
        dentist.setDentistId(rs.getInt("dentist_id"));
        dentist.setFirstName(rs.getString("first_name"));
        dentist.setLastName(rs.getString("last_name"));
        dentist.setSpecialization(rs.getString("specialization"));
        dentist.setLicenseNumber(rs.getString("license_number"));
        dentist.setContactNumber(rs.getString("contact_number"));
        dentist.setEmail(rs.getString("email"));
        dentist.setConsultationFee(rs.getDouble("consultation_fee"));
        dentist.setWorkingDays(rs.getString("working_days"));
        dentist.setWorkingHours(rs.getString("working_hours"));
        dentist.setActive(rs.getBoolean("is_active"));
        return dentist;
    }
}