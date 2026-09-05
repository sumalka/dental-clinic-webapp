package com.dentalclinic.dao;

import com.dentalclinic.model.Staff;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY first_name, last_name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                staffList.add(extractStaffFromResultSet(rs));
            }
            System.out.println("Loaded " + staffList.size() + " staff members");
        } catch (SQLException e) {
            System.err.println("Error getting staff: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    public Staff getStaffById(int staffId) {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractStaffFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Staff getStaffByUsername(String username) {
        String sql = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractStaffFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ? AND status = 'Active'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Staff authenticated: " + username);
                return extractStaffFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating staff: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO staff (first_name, last_name, username, password, email, phone, role, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setString(3, staff.getUsername());
            stmt.setString(4, staff.getPassword());
            stmt.setString(5, staff.getEmail());
            stmt.setString(6, staff.getPhone());
            stmt.setString(7, staff.getRole());
            stmt.setString(8, staff.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    staff.setStaffId(rs.getInt(1));
                }
                System.out.println("Staff added: " + staff.getUsername());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET first_name = ?, last_name = ?, username = ?, password = ?, " +
                "email = ?, phone = ?, role = ?, status = ? WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setString(3, staff.getUsername());
            stmt.setString(4, staff.getPassword());
            stmt.setString(5, staff.getEmail());
            stmt.setString(6, staff.getPhone());
            stmt.setString(7, staff.getRole());
            stmt.setString(8, staff.getStatus());
            stmt.setInt(9, staff.getStaffId());

            int rows = stmt.executeUpdate();
            System.out.println("Staff updated: " + staff.getUsername() + ", rows: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            int rows = stmt.executeUpdate();
            System.out.println("Staff deleted: ID " + staffId + ", rows: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStaffStatus(int staffId, String status) {
        String sql = "UPDATE staff SET status = ? WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, staffId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating staff status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Staff extractStaffFromResultSet(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setFirstName(rs.getString("first_name"));
        staff.setLastName(rs.getString("last_name"));
        staff.setUsername(rs.getString("username"));
        staff.setPassword(rs.getString("password"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone"));
        staff.setRole(rs.getString("role"));
        staff.setStatus(rs.getString("status"));
        return staff;
    }
}