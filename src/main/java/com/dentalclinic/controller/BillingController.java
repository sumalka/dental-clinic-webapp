package com.dentalclinic.controller;

import com.dentalclinic.model.Bill;
import com.dentalclinic.service.BillService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillingController extends HttpServlet {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();
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
            System.out.println("=== BILLING GET REQUEST ===");
            System.out.println("Action: " + action);

            if (action == null || action.equals("list")) {
                List<Bill> bills = billService.getAllBills();
                System.out.println("Retrieved " + bills.size() + " bills from service");

                // Debug: Print each bill
                for (Bill bill : bills) {
                    System.out.println("  - Bill ID: " + bill.getBillId() +
                            ", Invoice: " + bill.getInvoiceNumber() +
                            ", Patient: " + bill.getPatientName() +
                            ", Amount: " + bill.getTotalAmount());
                }

                String json = gson.toJson(bills);
                System.out.println("JSON length: " + json.length());
                out.print(json);

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
                int billId = Integer.parseInt(billIdParam);
                Bill bill = billService.getBillById(billId);
                if (bill != null) {
                    out.print(gson.toJson(bill));
                } else {
                    out.print("{\"error\": \"Bill not found\"}");
                }

            } else if (action.equals("filter")) {
                String search = request.getParameter("search");
                String status = request.getParameter("status");
                String dateFrom = request.getParameter("dateFrom");
                String dateTo = request.getParameter("dateTo");
                List<Bill> bills = billService.getBillsWithFilters(search, status, dateFrom, dateTo);
                out.print(gson.toJson(bills));

            } else {
                out.print("{\"error\": \"Invalid action. Valid: list, getByAppointment, stats, todayRevenue, view, filter\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    @Override
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

            String requestBody = sb.toString();
            System.out.println("=== BILLING POST REQUEST ===");
            System.out.println("Body: " + requestBody);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
            String action = json.has("action") ? json.get("action").getAsString() : null;
            System.out.println("Action: " + action);

            if ("generate".equals(action)) {
                if (!json.has("appointmentId")) {
                    out.print("{\"success\": false, \"message\": \"Missing appointmentId\"}");
                    return;
                }
                int appointmentId = json.get("appointmentId").getAsInt();
                System.out.println("Generating bill for appointment: " + appointmentId);

                Bill bill = billService.generateBill(appointmentId);

                JsonObject result = new JsonObject();
                if (bill != null) {
                    result.addProperty("success", true);
                    result.addProperty("message", "Bill generated successfully");
                    result.addProperty("billId", bill.getBillId());
                    result.addProperty("invoiceNumber", bill.getInvoiceNumber());
                    result.addProperty("totalAmount", bill.getTotalAmount());
                    System.out.println("Bill generated: " + bill.getInvoiceNumber() + " with ID: " + bill.getBillId());
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "Failed to generate bill. Please check logs for details.");
                    System.err.println("Failed to generate bill for appointment: " + appointmentId);
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
            result.addProperty("message", "Error: " + e.getMessage());
            out.print(result.toString());
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