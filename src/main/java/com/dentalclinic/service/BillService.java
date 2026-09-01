package com.dentalclinic.service;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.dao.BillDAO;
import com.dentalclinic.dao.DentistDAO;
import com.dentalclinic.dao.TreatmentDAO;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Bill;
import com.dentalclinic.model.Dentist;
import com.dentalclinic.model.Treatment;
import com.google.gson.JsonObject;
import java.util.List;

public class BillService {
    private final BillDAO billDAO = new BillDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }

    public Bill getBillByAppointmentId(int appointmentId) {
        return billDAO.getBillByAppointmentId(appointmentId);
    }

    public Bill getBillById(int billId) {
        // Add this method to BillDAO if needed
        return null;
    }

    public Bill generateBill(int appointmentId) {
        Bill existingBill = billDAO.getBillByAppointmentId(appointmentId);
        if (existingBill != null) {
            return existingBill;
        }

        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        if (appointment == null) {
            return null;
        }

        Dentist dentist = dentistDAO.getDentistById(appointment.getDentistId());
        Treatment treatment = null;
        if (appointment.getTreatmentId() != null) {
            treatment = treatmentDAO.getTreatmentById(appointment.getTreatmentId());
        }

        double treatmentCost = treatment != null ? treatment.getBasePrice() : 0;
        double consultationFee = dentist != null ? dentist.getConsultationFee() : 0;
        double subtotal = treatmentCost + consultationFee;
        double taxAmount = subtotal * 0.10;
        double totalAmount = subtotal + taxAmount;

        Bill bill = new Bill();
        bill.setAppointmentId(appointmentId);
        bill.setPatientId(appointment.getPatientId());
        bill.setPatientName(appointment.getPatientName());
        bill.setDentistName(appointment.getDentistName());
        bill.setTreatmentName(treatment != null ? treatment.getTreatmentName() : "Consultation");
        bill.setTreatmentCost(treatmentCost);
        bill.setConsultationFee(consultationFee);
        bill.setSubtotal(subtotal);
        bill.setTaxAmount(taxAmount);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentStatus("PENDING");

        if (billDAO.addBill(bill)) {
            return bill;
        }
        return null;
    }

    public boolean processPayment(int billId, String paymentMethod) {
        return billDAO.updateBillPayment(billId, paymentMethod, "PAID");
    }

    public double getTodayRevenue() {
        return billDAO.getTodayRevenue();
    }

    public int getPendingBillsCount() {
        return billDAO.getPendingBillsCount();
    }

    public JsonObject getBillingStats() {
        JsonObject stats = new JsonObject();
        stats.addProperty("pendingBills", billDAO.getPendingBillsCount());
        stats.addProperty("todayRevenue", billDAO.getTodayRevenue());
        return stats;
    }
}