package com.dentalclinic.controller;

import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.BillService;
import com.dentalclinic.service.PatientService;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ReportController extends HttpServlet {
    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final BillService billService = new BillService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("daily")) {
                JsonObject report = new JsonObject();
                report.addProperty("totalPatients", patientService.getTotalPatientCount());
                report.addProperty("todayAppointments", appointmentService.getTodayAppointmentCount());
                report.addProperty("pendingBills", billService.getPendingBillsCount());
                report.addProperty("todayRevenue", billService.getTodayRevenue());
                report.addProperty("success", true);
                out.print(report.toString());

            } else if (action.equals("stats")) {
                JsonObject stats = new JsonObject();
                stats.addProperty("totalPatients", patientService.getTotalPatientCount());
                stats.addProperty("todayAppointments", appointmentService.getTodayAppointmentCount());
                stats.addProperty("pendingBills", billService.getPendingBillsCount());
                stats.addProperty("todayRevenue", billService.getTodayRevenue());
                stats.addProperty("success", true);
                out.print(stats.toString());

            } else {
                out.print("{\"error\": \"Invalid action. Valid: daily, stats\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}