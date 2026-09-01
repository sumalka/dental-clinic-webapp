package com.dentalclinic.model;

public class Treatment {
    private int treatmentId;
    private String treatmentName;
    private String category;
    private String description;
    private double basePrice;
    private int durationMinutes;
    private boolean isActive;

    public Treatment() {}

    public Treatment(String treatmentName, String category, String description,
                     double basePrice, int durationMinutes) {
        this.treatmentName = treatmentName;
        this.category = category;
        this.description = description;
        this.basePrice = basePrice;
        this.durationMinutes = durationMinutes;
        this.isActive = true;
    }

    // Getters and Setters
    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}