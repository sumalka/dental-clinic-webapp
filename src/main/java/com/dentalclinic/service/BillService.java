package com.dentalclinic.service;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.dao.BillDAO;
import com.dentalclinic.dao.DentistDAO;
import com.dentalclinic.dao.TreatmentDAO;
import com.dentalclinic.dao.PatientDAO;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Bill;
import com.dentalclinic.model.Dentist;
import com.dentalclinic.model.Treatment;
import com.dentalclinic.model.Patient;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillService {
    private final BillDAO billDAO = new BillDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    public List<Bill> getAllBills() {
        List<Bill> bills = billDAO.getAllBills();
        System.out.println("BillService.getAllBills() returned " + bills.size() + " bills");
        return bills;
    }

    public List<Bill> getBillsWithFilters(String search, String status, String dateFrom, String dateTo) {
        return billDAO.getBillsWithFilters(search, status, dateFrom, dateTo);
    }

    public Bill getBillByAppointmentId(int appointmentId) {
        return billDAO.getBillByAppointmentId(appointmentId);
    }

    public Bill getBillById(int billId) {
        return billDAO.getBillById(billId);
    }

    public Bill generateBill(int appointmentId) {
        System.out.println("=== GENERATING BILL FOR APPOINTMENT ID: " + appointmentId + " ===");

        // Check if bill already exists
        Bill existingBill = billDAO.getBillByAppointmentId(appointmentId);
        if (existingBill != null) {
            System.out.println("Bill already exists for appointment " + appointmentId);
            return existingBill;
        }

        // Get appointment details
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment == null) {
            System.err.println("❌ Appointment not found: " + appointmentId);
            return null;
        }
        System.out.println("✅ Found appointment: " + appointment.getAppointmentNumber());

        // Get patient details
        Patient patient = patientDAO.getPatientById(appointment.getPatientId());
        if (patient == null) {
            System.err.println("❌ Patient not found: " + appointment.getPatientId());
            return null;
        }
        System.out.println("✅ Found patient: " + patient.getFullName());

        // Get dentist details
        Dentist dentist = dentistDAO.getDentistById(appointment.getDentistId());
        if (dentist == null) {
            System.err.println("❌ Dentist not found: " + appointment.getDentistId());
            return null;
        }
        System.out.println("✅ Found dentist: " + dentist.getFullName());

        // Get treatment details
        Treatment treatment = null;
        if (appointment.getTreatmentId() != null) {
            treatment = treatmentDAO.getTreatmentById(appointment.getTreatmentId());
            if (treatment != null) {
                System.out.println("✅ Found treatment: " + treatment.getTreatmentName());
            }
        }

        // Calculate amounts
        double treatmentCost = treatment != null ? treatment.getBasePrice() : 0;
        double consultationFee = dentist != null ? dentist.getConsultationFee() : 0;
        double subtotal = treatmentCost + consultationFee;
        double taxRate = 10.0;
        double taxAmount = subtotal * (taxRate / 100);
        double totalAmount = subtotal + taxAmount;

        System.out.println("Treatment Cost: $" + treatmentCost);
        System.out.println("Consultation Fee: $" + consultationFee);
        System.out.println("Subtotal: $" + subtotal);
        System.out.println("Tax: $" + taxAmount);
        System.out.println("Total: $" + totalAmount);

        // Create bill
        Bill bill = new Bill();
        bill.setAppointmentId(appointmentId);
        bill.setPatientId(appointment.getPatientId());
        bill.setPatientName(patient.getFullName());
        bill.setPatientEmail(patient.getEmail() != null ? patient.getEmail() : "");
        bill.setPatientPhone(patient.getContactNumber() != null ? patient.getContactNumber() : "");
        bill.setDentistName(dentist.getFullName());
        bill.setTreatmentName(treatment != null ? treatment.getTreatmentName() : "Consultation");
        bill.setRoomNumber("N/A");
        bill.setTreatmentCost(treatmentCost);
        bill.setConsultationFee(consultationFee);
        bill.setSubtotal(subtotal);
        bill.setTaxRate(taxRate);
        bill.setTaxAmount(taxAmount);
        bill.setDiscountAmount(0);
        bill.setDiscountReason(null);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentMethod(null);
        bill.setPaymentStatus("PENDING");
        bill.setIssuedBy(1);
        bill.setInvoiceNumber(generateInvoiceNumber());
        bill.setBillDate(LocalDateTime.now());

        System.out.println("Generated invoice number: " + bill.getInvoiceNumber());

        // Save bill to database
        boolean success = billDAO.addBill(bill);
        if (success) {
            System.out.println("✅ Bill saved successfully with ID: " + bill.getBillId());
            System.out.println("✅ Bill invoice number: " + bill.getInvoiceNumber());
            return bill;
        } else {
            System.err.println("❌ Failed to save bill to database");
            return null;
        }
    }

    private String generateInvoiceNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = (int)(Math.random() * 10000);
        return "INV-" + dateStr + "-" + String.format("%04d", randomNum);
    }

    public boolean processPayment(int billId, String paymentMethod) {
        return billDAO.updateBillPayment(billId, paymentMethod, "PAID");
    }

    public double getTotalRevenue() {
        return billDAO.getTotalRevenue();
    }

    public double getTodayRevenue() {
        return billDAO.getTodayRevenue();
    }

    public double getPendingPaymentsTotal() {
        return billDAO.getPendingPaymentsTotal();
    }

    public int getTotalInvoices() {
        return billDAO.getTotalInvoices();
    }

    public int getPendingBillsCount() {
        return billDAO.getPendingBillsCount();
    }

    public JsonObject getBillingStats() {
        JsonObject stats = new JsonObject();
        stats.addProperty("totalRevenue", billDAO.getTotalRevenue());
        stats.addProperty("todayRevenue", billDAO.getTodayRevenue());
        stats.addProperty("pendingTotal", billDAO.getPendingPaymentsTotal());
        stats.addProperty("totalInvoices", billDAO.getTotalInvoices());
        stats.addProperty("pendingCount", billDAO.getPendingBillsCount());
        stats.addProperty("success", true);
        return stats;
    }
}