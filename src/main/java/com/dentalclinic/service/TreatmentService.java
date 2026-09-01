package com.dentalclinic.service;

import com.dentalclinic.dao.TreatmentDAO;
import com.dentalclinic.model.Treatment;
import java.util.List;

public class TreatmentService {
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    public List<Treatment> getAllTreatments() {
        return treatmentDAO.getAllTreatments();
    }

    public Treatment getTreatmentById(int treatmentId) {
        return treatmentDAO.getTreatmentById(treatmentId);
    }
}