package com.dentalclinic.dao;

import com.dentalclinic.model.Bill;
import com.dentalclinic.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
            System.out.println("✅ Loaded " + bills.size() + " bills from database");
        } catch (SQLException e) {
            System.err.println("❌ Error getting bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    public List<Bill> getBillsWithFilters(String search, String status, String dateFrom, String dateTo) {
        List<Bill> bills = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM bills WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (patient_name LIKE ? OR invoice_number LIKE ? OR treatment_name LIKE ?)");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        if (status != null && !status.isEmpty() && !status.equals("ALL")) {
            sql.append(" AND payment_status = ?");
            params.add(status);
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append(" AND DATE(bill_date) >= ?");
            params.add(dateFrom);
        }
        if (dateTo != null && !dateTo.isEmpty()) {
            sql.append(" AND DATE(bill_date) <= ?");
            params.add(dateTo);
        }

        sql.append(" ORDER BY bill_date DESC");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
            System.out.println("✅ Loaded " + bills.size() + " bills with filters");
        } catch (SQLException e) {
            System.err.println("❌ Error getting bills with filters: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }

    public Bill getBillByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM bills WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Found bill for appointment " + appointmentId);
                return extractBillFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting bill by appointment ID: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("⚠️ No bill found for appointment " + appointmentId);
        return null;
    }

    public Bill getBillById(int billId) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBillFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting bill by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addBill(Bill bill) {
        String sql = "INSERT INTO bills (appointment_id, patient_id, patient_name, patient_email, patient_phone, " +
                "dentist_name, treatment_name, room_number, treatment_cost, consultation_fee, subtotal, " +
                "tax_rate, tax_amount, discount_amount, discount_reason, total_amount, payment_method, " +
                "payment_status, issued_by, invoice_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("=== INSERTING BILL ===");
            System.out.println("Appointment ID: " + bill.getAppointmentId());
            System.out.println("Patient ID: " + bill.getPatientId());
            System.out.println("Patient Name: " + bill.getPatientName());
            System.out.println("Patient Email: " + bill.getPatientEmail());
            System.out.println("Patient Phone: " + bill.getPatientPhone());
            System.out.println("Dentist Name: " + bill.getDentistName());
            System.out.println("Treatment Name: " + bill.getTreatmentName());
            System.out.println("Room Number: " + bill.getRoomNumber());
            System.out.println("Treatment Cost: " + bill.getTreatmentCost());
            System.out.println("Consultation Fee: " + bill.getConsultationFee());
            System.out.println("Subtotal: " + bill.getSubtotal());
            System.out.println("Tax Rate: " + bill.getTaxRate());
            System.out.println("Tax Amount: " + bill.getTaxAmount());
            System.out.println("Discount Amount: " + bill.getDiscountAmount());
            System.out.println("Discount Reason: " + bill.getDiscountReason());
            System.out.println("Total Amount: " + bill.getTotalAmount());
            System.out.println("Payment Method: " + bill.getPaymentMethod());
            System.out.println("Payment Status: " + bill.getPaymentStatus());
            System.out.println("Issued By: " + bill.getIssuedBy());
            System.out.println("Invoice Number: " + bill.getInvoiceNumber());
            System.out.println("======================");

            stmt.setInt(1, bill.getAppointmentId());
            stmt.setInt(2, bill.getPatientId());
            stmt.setString(3, bill.getPatientName());
            stmt.setString(4, bill.getPatientEmail() != null ? bill.getPatientEmail() : "");
            stmt.setString(5, bill.getPatientPhone() != null ? bill.getPatientPhone() : "");
            stmt.setString(6, bill.getDentistName());
            stmt.setString(7, bill.getTreatmentName());
            stmt.setString(8, bill.getRoomNumber() != null ? bill.getRoomNumber() : "N/A");
            stmt.setDouble(9, bill.getTreatmentCost());
            stmt.setDouble(10, bill.getConsultationFee());
            stmt.setDouble(11, bill.getSubtotal());
            stmt.setDouble(12, bill.getTaxRate());
            stmt.setDouble(13, bill.getTaxAmount());
            stmt.setDouble(14, bill.getDiscountAmount());
            stmt.setString(15, bill.getDiscountReason());
            stmt.setDouble(16, bill.getTotalAmount());
            stmt.setString(17, bill.getPaymentMethod());
            stmt.setString(18, bill.getPaymentStatus());
            stmt.setInt(19, bill.getIssuedBy());
            stmt.setString(20, bill.getInvoiceNumber());

            int affectedRows = stmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    bill.setBillId(rs.getInt(1));
                    System.out.println("✅ Bill added successfully with ID: " + bill.getBillId());
                }
                return true;
            }
            System.err.println("❌ No rows affected when adding bill");
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error adding bill: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBillPayment(int billId, String paymentMethod, String status) {
        String sql = "UPDATE bills SET payment_method = ?, payment_status = ? WHERE bill_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentMethod);
            stmt.setString(2, status);
            stmt.setInt(3, billId);
            int rows = stmt.executeUpdate();
            System.out.println("Updated bill payment: " + billId + ", rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating bill payment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as revenue FROM bills WHERE payment_status = 'PAID'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
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
            System.err.println("❌ Error getting today's revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getPendingPaymentsTotal() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM bills WHERE payment_status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting pending payments total: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTotalInvoices() {
        String sql = "SELECT COUNT(*) as count FROM bills";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting total invoices: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
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
            System.err.println("❌ Error getting pending bills count: " + e.getMessage());
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
        bill.setPatientEmail(rs.getString("patient_email"));
        bill.setPatientPhone(rs.getString("patient_phone"));
        bill.setDentistName(rs.getString("dentist_name"));
        bill.setTreatmentName(rs.getString("treatment_name"));
        bill.setRoomNumber(rs.getString("room_number"));
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
        bill.setInvoiceNumber(rs.getString("invoice_number"));
        Timestamp timestamp = rs.getTimestamp("bill_date");
        if (timestamp != null) {
            bill.setBillDate(timestamp.toLocalDateTime());
        }
        return bill;
    }
}