package com.dentalclinic.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bill {
    private int billId;
    private int appointmentId;
    private int patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String dentistName;
    private String treatmentName;
    private String roomNumber;
    private double treatmentCost;
    private double consultationFee;
    private double subtotal;
    private double taxRate;
    private double taxAmount;
    private double discountAmount;
    private String discountReason;
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime billDate;
    private int issuedBy;
    private String invoiceNumber;

    public Bill() {
        this.taxRate = 10.0;
        this.paymentStatus = "PENDING";
        this.billDate = LocalDateTime.now();
        this.treatmentCost = 0.0;
        this.consultationFee = 0.0;
        this.subtotal = 0.0;
        this.taxAmount = 0.0;
        this.discountAmount = 0.0;
        this.totalAmount = 0.0;
        this.roomNumber = "N/A";
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public double getTreatmentCost() { return treatmentCost; }
    public void setTreatmentCost(double treatmentCost) { this.treatmentCost = treatmentCost; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getDiscountReason() { return discountReason; }
    public void setDiscountReason(String discountReason) { this.discountReason = discountReason; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getBillDate() { return billDate; }
    public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }

    public int getIssuedBy() { return issuedBy; }
    public void setIssuedBy(int issuedBy) { this.issuedBy = issuedBy; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getFormattedBillDate() {
        if (billDate == null) return "N/A";
        return billDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    public String getPaymentStatusBadge() {
        if ("PAID".equalsIgnoreCase(paymentStatus)) {
            return "paid";
        } else if ("PENDING".equalsIgnoreCase(paymentStatus)) {
            return "pending";
        } else {
            return "overdue";
        }
    }
}