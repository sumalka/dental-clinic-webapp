package com.dentalclinic.controller;

import com.dentalclinic.model.Dentist;
import com.dentalclinic.service.DentistService;
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

public class DentistController extends HttpServlet {
    private final Gson gson = new GsonBuilder().create();
    private final DentistService dentistService = new DentistService();

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
                List<Dentist> dentists = dentistService.getAllDentists();
                String json = gson.toJson(dentists);
                System.out.println("Sending " + dentists.size() + " dentists with status: " +
                        dentists.stream().map(d -> d.getDentistId() + ":" + d.isActive()).toList());
                out.print(json);
                System.out.println("Listed " + dentists.size() + " dentists");
            } else if (action.equals("getById")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isEmpty()) {
                    out.print("{\"error\": \"Missing dentist ID\"}");
                    return;
                }
                int id = Integer.parseInt(idParam);
                Dentist dentist = dentistService.getDentistById(id);
                if (dentist != null) {
                    String json = gson.toJson(dentist);
                    System.out.println("Retrieved dentist: ID=" + dentist.getDentistId() +
                            ", Name=" + dentist.getFullName() +
                            ", Active=" + dentist.isActive() +
                            ", JSON=" + json);
                    out.print(json);
                } else {
                    out.print("{\"error\": \"Dentist not found\"}");
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

            // If _method is found in URL, handle accordingly
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

            // Also check for _method in request body (for form submissions)
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

            // Regular POST - Add new dentist
            if (requestBody == null || requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Dentist dentist = new Dentist();
            dentist.setFirstName(json.get("firstName").getAsString());
            dentist.setLastName(json.get("lastName").getAsString());
            dentist.setSpecialization(json.get("specialization").getAsString());
            dentist.setLicenseNumber(json.get("licenseNumber").getAsString());
            dentist.setContactNumber(json.get("contactNumber").getAsString());
            dentist.setEmail(json.get("email").getAsString());
            dentist.setConsultationFee(json.get("consultationFee").getAsDouble());

            if (json.has("workingDays") && !json.get("workingDays").isJsonNull()) {
                dentist.setWorkingDays(json.get("workingDays").getAsString());
            }
            if (json.has("workingHours") && !json.get("workingHours").isJsonNull()) {
                dentist.setWorkingHours(json.get("workingHours").getAsString());
            }
            dentist.setActive(true);

            boolean success = dentistService.addDentist(dentist);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Dentist added successfully");
                result.addProperty("dentistId", dentist.getDentistId());
                result.addProperty("fullName", dentist.getFullName());
                System.out.println("Dentist saved with ID: " + dentist.getDentistId() + ", Active: " + dentist.isActive());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to add dentist. Please check all required fields.");
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
            System.out.println("Received update dentist data: " + requestBody);

            if (requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Dentist dentist = new Dentist();
            dentist.setDentistId(json.get("dentistId").getAsInt());
            dentist.setFirstName(json.get("firstName").getAsString());
            dentist.setLastName(json.get("lastName").getAsString());
            dentist.setSpecialization(json.get("specialization").getAsString());
            dentist.setLicenseNumber(json.get("licenseNumber").getAsString());
            dentist.setContactNumber(json.get("contactNumber").getAsString());
            dentist.setEmail(json.get("email").getAsString());
            dentist.setConsultationFee(json.get("consultationFee").getAsDouble());

            if (json.has("workingDays") && !json.get("workingDays").isJsonNull()) {
                dentist.setWorkingDays(json.get("workingDays").getAsString());
            }
            if (json.has("workingHours") && !json.get("workingHours").isJsonNull()) {
                dentist.setWorkingHours(json.get("workingHours").getAsString());
            }
            if (json.has("active") && !json.get("active").isJsonNull()) {
                dentist.setActive(json.get("active").getAsBoolean());
            }

            boolean success = dentistService.updateDentist(dentist);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Dentist updated successfully");
                result.addProperty("active", dentist.isActive());
                System.out.println("Dentist updated: ID " + dentist.getDentistId() + ", Active: " + dentist.isActive());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to update dentist");
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
                out.print("{\"success\": false, \"message\": \"Missing dentist ID\"}");
                return;
            }

            int dentistId = Integer.parseInt(idParam);
            boolean success = dentistService.deleteDentist(dentistId);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Dentist deleted successfully");
                System.out.println("Dentist deleted: ID " + dentistId);
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to delete dentist");
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
                out.print("{\"success\": false, \"message\": \"Missing dentist ID\"}");
                return;
            }

            int dentistId = Integer.parseInt(idParam);

            // Get the existing dentist
            Dentist dentist = dentistService.getDentistById(dentistId);

            if (dentist == null) {
                out.print("{\"success\": false, \"message\": \"Dentist not found\"}");
                return;
            }

            // Toggle the status
            boolean newStatus = !dentist.isActive();
            dentist.setActive(newStatus);
            System.out.println("Toggling dentist " + dentistId + " from " + !newStatus + " to: " + newStatus);

            // Update in database
            boolean success = dentistService.updateDentist(dentist);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Dentist " + (newStatus ? "activated" : "deactivated") + " successfully");
                result.addProperty("active", newStatus);
                result.addProperty("dentistId", dentistId);
                System.out.println("Dentist status toggled: ID " + dentistId + " -> " + (newStatus ? "Active" : "Inactive"));
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to toggle dentist status. Check database connection.");
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