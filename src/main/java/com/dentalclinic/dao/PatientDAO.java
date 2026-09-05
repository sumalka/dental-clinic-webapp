package com.dentalclinic.dao;

import com.dentalclinic.model.Patient;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PatientDAO {
    private static final Logger LOGGER = Logger.getLogger(PatientDAO.class.getName());

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, contact_number, " +
                "email, address, city, state, postal_code, nationality, id_type, id_number, " +
                "emergency_contact_name, emergency_contact_number, medical_notes, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getContactNumber());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getCity());
            stmt.setString(9, patient.getState());
            stmt.setString(10, patient.getPostalCode());
            stmt.setString(11, patient.getNationality());
            stmt.setString(12, patient.getIdType());
            stmt.setString(13, patient.getIdNumber());
            stmt.setString(14, patient.getEmergencyContactName());
            stmt.setString(15, patient.getEmergencyContactNumber());
            stmt.setString(16, patient.getMedicalNotes());
            stmt.setString(17, patient.getStatus() != null ? patient.getStatus() : "Active");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    patient.setPatientId(rs.getInt(1));
                    LOGGER.info("Patient added with ID: " + patient.getPatientId());
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error adding patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
            LOGGER.info("Loaded " + patients.size() + " patients from database");
        } catch (SQLException e) {
            LOGGER.severe("Error getting patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPatientFromResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting patient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR contact_number LIKE ? OR id_number LIKE ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error searching patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, " +
                "contact_number = ?, email = ?, address = ?, city = ?, state = ?, postal_code = ?, " +
                "nationality = ?, id_type = ?, id_number = ?, emergency_contact_name = ?, " +
                "emergency_contact_number = ?, medical_notes = ?, status = ? WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getContactNumber());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getCity());
            stmt.setString(9, patient.getState());
            stmt.setString(10, patient.getPostalCode());
            stmt.setString(11, patient.getNationality());
            stmt.setString(12, patient.getIdType());
            stmt.setString(13, patient.getIdNumber());
            stmt.setString(14, patient.getEmergencyContactName());
            stmt.setString(15, patient.getEmergencyContactNumber());
            stmt.setString(16, patient.getMedicalNotes());
            stmt.setString(17, patient.getStatus());
            stmt.setInt(18, patient.getPatientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error updating patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                LOGGER.info("Patient deleted: ID " + patientId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalPatientCount() {
        String sql = "SELECT COUNT(*) as count FROM patients";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting patient count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getActivePatientCount() {
        String sql = "SELECT COUNT(*) as count FROM patients WHERE status = 'Active'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting active patient count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getNewThisMonthCount() {
        String sql = "SELECT COUNT(*) as count FROM patients WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting new this month count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setGender(rs.getString("gender"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setCity(rs.getString("city"));
        patient.setState(rs.getString("state"));
        patient.setPostalCode(rs.getString("postal_code"));
        patient.setNationality(rs.getString("nationality"));
        patient.setIdType(rs.getString("id_type"));
        patient.setIdNumber(rs.getString("id_number"));
        patient.setEmergencyContactName(rs.getString("emergency_contact_name"));
        patient.setEmergencyContactNumber(rs.getString("emergency_contact_number"));
        patient.setMedicalNotes(rs.getString("medical_notes"));
        patient.setStatus(rs.getString("status"));
        return patient;
    }
}