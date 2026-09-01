package com.dentalclinic.dao;

import com.dentalclinic.model.Bill;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public Bill getBillByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM bills WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBillFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
    }

    public boolean addBill(Bill bill) {
        String sql = "INSERT INTO bills (appointment_id, patient_id, patient_name, dentist_name, treatment_name, " +
                "treatment_cost, consultation_fee, subtotal, tax_rate, tax_amount, discount_amount, " +
                "discount_reason, total_amount, payment_method, payment_status, issued_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, bill.getAppointmentId());
            stmt.setInt(2, bill.getPatientId());
            stmt.setString(3, bill.getPatientName());
            stmt.setString(4, bill.getDentistName());
            stmt.setString(5, bill.getTreatmentName());
            stmt.setDouble(6, bill.getTreatmentCost());
            stmt.setDouble(7, bill.getConsultationFee());
            stmt.setDouble(8, bill.getSubtotal());
            stmt.setDouble(9, bill.getTaxRate());
            stmt.setDouble(10, bill.getTaxAmount());
            stmt.setDouble(11, bill.getDiscountAmount());
            stmt.setString(12, bill.getDiscountReason());
            stmt.setDouble(13, bill.getTotalAmount());
            stmt.setString(14, bill.getPaymentMethod());
            stmt.setString(15, bill.getPaymentStatus());
            stmt.setInt(16, bill.getIssuedBy());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    bill.setBillId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBillPayment(int billId, String paymentMethod, String status) {
        String sql = "UPDATE bills SET payment_method = ?, payment_status = ? WHERE bill_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentMethod);
            stmt.setString(2, status);
            stmt.setInt(3, billId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTodayRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as revenue FROM bills WHERE DATE(bill_date) = CURDATE() AND payment_status = 'PAID'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getPendingBillsCount() {
        String sql = "SELECT COUNT(*) as count FROM bills WHERE payment_status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setAppointmentId(rs.getInt("appointment_id"));
        bill.setPatientId(rs.getInt("patient_id"));
        bill.setPatientName(rs.getString("patient_name"));
        bill.setDentistName(rs.getString("dentist_name"));
        bill.setTreatmentName(rs.getString("treatment_name"));
        bill.setTreatmentCost(rs.getDouble("treatment_cost"));
        bill.setConsultationFee(rs.getDouble("consultation_fee"));
        bill.setSubtotal(rs.getDouble("subtotal"));
        bill.setTaxRate(rs.getDouble("tax_rate"));
        bill.setTaxAmount(rs.getDouble("tax_amount"));
        bill.setDiscountAmount(rs.getDouble("discount_amount"));
        bill.setDiscountReason(rs.getString("discount_reason"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setIssuedBy(rs.getInt("issued_by"));
        bill.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        return bill;
    }
}