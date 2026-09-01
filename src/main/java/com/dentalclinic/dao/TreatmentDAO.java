package com.dentalclinic.dao;

import com.dentalclinic.model.Treatment;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    public List<Treatment> getAllTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE is_active = TRUE ORDER BY treatment_name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                treatments.add(extractTreatmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return treatments;
    }

    public Treatment getTreatmentById(int treatmentId) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ? AND is_active = TRUE";

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