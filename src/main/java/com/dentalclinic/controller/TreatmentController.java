package com.dentalclinic.controller;

import com.dentalclinic.model.Treatment;
import com.dentalclinic.service.TreatmentService;
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
import java.util.List;

public class TreatmentController extends HttpServlet {
    private final Gson gson = new GsonBuilder().create();
    private final TreatmentService treatmentService = new TreatmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if (action == null || action.equals("list")) {
                List<Treatment> treatments = treatmentService.getAllTreatments();
                String json = gson.toJson(treatments);
                System.out.println("Sending " + treatments.size() + " treatments");
                out.print(json);
            } else if (action.equals("getById")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isEmpty()) {
                    out.print("{\"error\": \"Missing treatment ID\"}");
                    return;
                }
                int id = Integer.parseInt(idParam);
                Treatment treatment = treatmentService.getTreatmentById(id);
                if (treatment != null) {
                    out.print(gson.toJson(treatment));
                } else {
                    out.print("{\"error\": \"Treatment not found\"}");
                }
            } else {
                out.print("{\"error\": \"Invalid action. Valid: list, getById\"}");
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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();

        try {
            // Check for _method in URL parameter first
            String methodOverride = request.getParameter("_method");

            System.out.println("Method override from URL: " + methodOverride);

            if ("PUT".equalsIgnoreCase(methodOverride)) {
                handlePut(request, response);
                return;
            }

            if ("DELETE".equalsIgnoreCase(methodOverride)) {
                handleDelete(request, response);
                return;
            }

            if ("PATCH".equalsIgnoreCase(methodOverride)) {
                handlePatch(request, response);
                return;
            }

            // Read request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            // Check if body contains _method
            if (requestBody != null && !requestBody.isEmpty() && requestBody.contains("_method=")) {
                String[] parts = requestBody.split("&");
                for (String part : parts) {
                    if (part.startsWith("_method=")) {
                        String method = part.substring(8);
                        System.out.println("Method override from body: " + method);
                        if ("PATCH".equalsIgnoreCase(method)) {
                            String idParam = request.getParameter("id");
                            if (idParam != null && !idParam.isEmpty()) {
                                handlePatch(request, response);
                                return;
                            }
                        }
                        if ("DELETE".equalsIgnoreCase(method)) {
                            handleDelete(request, response);
                            return;
                        }
                        if ("PUT".equalsIgnoreCase(method)) {
                            handlePut(request, response);
                            return;
                        }
                    }
                }
            }

            // Regular POST - Add new treatment
            if (requestBody == null || requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Treatment treatment = new Treatment();
            treatment.setTreatmentName(json.get("treatmentName").getAsString());
            treatment.setCategory(json.get("category").getAsString());
            treatment.setDescription(json.get("description").getAsString());
            treatment.setBasePrice(json.get("basePrice").getAsDouble());
            treatment.setDurationMinutes(json.get("durationMinutes").getAsInt());
            treatment.setActive(true);

            boolean success = treatmentService.addTreatment(treatment);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Treatment added successfully");
                result.addProperty("treatmentId", treatment.getTreatmentId());
                result.addProperty("treatmentName", treatment.getTreatmentName());
                System.out.println("Treatment saved with ID: " + treatment.getTreatmentId());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to add treatment. Please check all required fields.");
            }
            out.print(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "Error: " + e.getMessage());
            out.print(result.toString());
        }
    }

    private void handlePut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
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
            System.out.println("Received update treatment data: " + requestBody);

            if (requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Treatment treatment = new Treatment();
            treatment.setTreatmentId(json.get("treatmentId").getAsInt());
            treatment.setTreatmentName(json.get("treatmentName").getAsString());
            treatment.setCategory(json.get("category").getAsString());
            treatment.setDescription(json.get("description").getAsString());
            treatment.setBasePrice(json.get("basePrice").getAsDouble());
            treatment.setDurationMinutes(json.get("durationMinutes").getAsInt());

            if (json.has("active") && !json.get("active").isJsonNull()) {
                treatment.setActive(json.get("active").getAsBoolean());
            }

            boolean success = treatmentService.updateTreatment(treatment);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Treatment updated successfully");
                result.addProperty("active", treatment.isActive());
                System.out.println("Treatment updated: ID " + treatment.getTreatmentId() + ", Active: " + treatment.isActive());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to update treatment");
            }
            out.print(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "Error: " + e.getMessage());
            out.print(result.toString());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            System.out.println("DELETE request for ID: " + idParam);

            if (idParam == null || idParam.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Missing treatment ID\"}");
                return;
            }

            int treatmentId = Integer.parseInt(idParam);
            boolean success = treatmentService.deleteTreatment(treatmentId);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Treatment deleted successfully");
                System.out.println("Treatment deleted: ID " + treatmentId);
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to delete treatment");
            }
            out.print(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("success", false);
            result.addProperty("message", "Error: " + e.getMessage());
            out.print(result.toString());
        }
    }

    private void handlePatch(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            System.out.println("PATCH request received for ID: " + idParam);

            if (idParam == null || idParam.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Missing treatment ID\"}");
                return;
            }

            int treatmentId = Integer.parseInt(idParam);

            // Get the existing treatment
            Treatment treatment = treatmentService.getTreatmentById(treatmentId);

            if (treatment == null) {
                out.print("{\"success\": false, \"message\": \"Treatment not found\"}");
                return;
            }

            // Toggle the status
            boolean newStatus = !treatment.isActive();
            treatment.setActive(newStatus);
            System.out.println("Toggling treatment " + treatmentId + " from " + !newStatus + " to: " + newStatus);

            // Update in database
            boolean success = treatmentService.updateTreatment(treatment);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Treatment " + (newStatus ? "activated" : "deactivated") + " successfully");
                result.addProperty("active", newStatus);
                result.addProperty("treatmentId", treatmentId);
                System.out.println("Treatment status toggled: ID " + treatmentId + " -> " + (newStatus ? "Active" : "Inactive"));
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to toggle treatment status. Check database connection.");
            }
            out.print(result.toString());

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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}