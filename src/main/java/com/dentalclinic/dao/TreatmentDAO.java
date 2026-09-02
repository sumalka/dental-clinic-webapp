package com.dentalclinic.dao;

import com.dentalclinic.model.Treatment;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    public List<Treatment> getAllTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY treatment_name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                treatments.add(extractTreatmentFromResultSet(rs));
            }
            System.out.println("Loaded " + treatments.size() + " treatments from database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treatments;
    }

    public Treatment getTreatmentById(int treatmentId) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, treatmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractTreatmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addTreatment(Treatment treatment) {
        String sql = "INSERT INTO treatments (treatment_name, category, description, " +
                "base_price, duration_minutes, is_active) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setString(2, treatment.getCategory());
            stmt.setString(3, treatment.getDescription());
            stmt.setDouble(4, treatment.getBasePrice());
            stmt.setInt(5, treatment.getDurationMinutes());
            stmt.setBoolean(6, treatment.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    treatment.setTreatmentId(rs.getInt(1));
                }
                System.out.println("Added treatment: " + treatment.getTreatmentName() + ", Active: " + treatment.isActive());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTreatment(Treatment treatment) {
        String sql = "UPDATE treatments SET treatment_name = ?, category = ?, description = ?, " +
                "base_price = ?, duration_minutes = ?, is_active = ? WHERE treatment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setString(2, treatment.getCategory());
            stmt.setString(3, treatment.getDescription());
            stmt.setDouble(4, treatment.getBasePrice());
            stmt.setInt(5, treatment.getDurationMinutes());
            stmt.setBoolean(6, treatment.isActive());
            stmt.setInt(7, treatment.getTreatmentId());

            int rows = stmt.executeUpdate();
            System.out.println("Updated treatment ID " + treatment.getTreatmentId() +
                    ", Active: " + treatment.isActive() + ", rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTreatment(int treatmentId) {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, treatmentId);
            int rows = stmt.executeUpdate();
            System.out.println("Deleted treatment ID " + treatmentId + ", rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Treatment extractTreatmentFromResultSet(ResultSet rs) throws SQLException {
        Treatment treatment = new Treatment();
        treatment.setTreatmentId(rs.getInt("treatment_id"));
        treatment.setTreatmentName(rs.getString("treatment_name"));
        treatment.setCategory(rs.getString("category"));
        treatment.setDescription(rs.getString("description"));
        treatment.setBasePrice(rs.getDouble("base_price"));
        treatment.setDurationMinutes(rs.getInt("duration_minutes"));
        treatment.setActive(rs.getBoolean("is_active"));
        return treatment;
    }
}