package com.dentalclinic.controller;

import com.dentalclinic.model.Bill;
import com.dentalclinic.service.BillService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class BillingController extends BaseController {
    private final BillService billService = new BillService();

    // NO @Override - this method does NOT override any superclass method
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
            if (action == null || action.equals("list")) {
                List<Bill> bills = billService.getAllBills();
                out.print(gson.toJson(bills));
                System.out.println("Listed " + bills.size() + " bills");

            } else if (action.equals("getByAppointment")) {
                String appointmentIdParam = request.getParameter("appointmentId");
                if (appointmentIdParam == null || appointmentIdParam.isEmpty()) {
                    out.print("{\"error\": \"Missing appointment ID\"}");
                    return;
                }
                int appointmentId = Integer.parseInt(appointmentIdParam);
                Bill bill = billService.getBillByAppointmentId(appointmentId);
                if (bill != null) {
                    out.print(gson.toJson(bill));
                } else {
                    out.print("{\"error\": \"Bill not found\"}");
                }

            } else if (action.equals("stats")) {
                JsonObject stats = billService.getBillingStats();
                stats.addProperty("success", true);
                out.print(stats.toString());

            } else if (action.equals("todayRevenue")) {
                double revenue = billService.getTodayRevenue();
                JsonObject result = new JsonObject();
                result.addProperty("todayRevenue", revenue);
                out.print(result.toString());

            } else if (action.equals("view")) {
                String billIdParam = request.getParameter("id");
                if (billIdParam == null || billIdParam.isEmpty()) {
                    out.print("{\"error\": \"Missing bill ID\"}");
                    return;
                }
                // Get bill by ID - we need to add this method to BillService
                // For now return error
                out.print("{\"error\": \"View bill by ID not implemented yet\"}");

            } else {
                out.print("{\"error\": \"Invalid action. Valid: list, getByAppointment, stats, todayRevenue, view\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    // NO @Override - this method does NOT override any superclass method
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            String action = json.has("action") ? json.get("action").getAsString() : null;

            if ("generate".equals(action)) {
                int appointmentId = json.get("appointmentId").getAsInt();
                Bill bill = billService.generateBill(appointmentId);

                JsonObject result = new JsonObject();
                if (bill != null) {
                    result.addProperty("success", true);
                    result.addProperty("message", "Bill generated successfully");
                    result.addProperty("billId", bill.getBillId());
                    result.addProperty("totalAmount", bill.getTotalAmount());
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "Failed to generate bill. Appointment may not exist or bill already generated.");
                }
                out.print(result.toString());

            } else if ("processPayment".equals(action)) {
                int billId = json.get("billId").getAsInt();
                String paymentMethod = json.get("paymentMethod").getAsString();

                boolean success = billService.processPayment(billId, paymentMethod);

                JsonObject result = new JsonObject();
                if (success) {
                    result.addProperty("success", true);
                    result.addProperty("message", "Payment processed successfully");
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "Payment processing failed");
                }
                out.print(result.toString());

            } else {
                out.print("{\"error\": \"Invalid action. Valid: generate, processPayment\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", e.getMessage());
            out.print(result.toString());
        }
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}