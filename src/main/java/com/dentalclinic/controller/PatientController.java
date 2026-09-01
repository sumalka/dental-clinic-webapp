package com.dentalclinic.controller;

import com.dentalclinic.model.Patient;
import com.dentalclinic.service.PatientService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientController extends HttpServlet {
    private final PatientService patientService = new PatientService();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    String date = in.nextString();
                    return LocalDate.parse(date);
                }
            })
            .create();

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
            if (action == null || action.equals("list")) {
                List<Patient> patients = patientService.getAllPatients();
                out.print(gson.toJson(patients));
                System.out.println("Listed " + patients.size() + " patients");

            } else if (action.equals("getById")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isEmpty()) {
                    out.print("{\"error\": \"Missing patient ID\"}");
                    return;
                }
                int id = Integer.parseInt(idParam);
                Patient patient = patientService.getPatientById(id);
                if (patient != null) {
                    out.print(gson.toJson(patient));
                } else {
                    out.print("{\"error\": \"Patient not found\"}");
                }

            } else if (action.equals("search")) {
                String searchTerm = request.getParameter("q");
                if (searchTerm == null) searchTerm = "";
                List<Patient> patients = patientService.searchPatients(searchTerm);
                out.print(gson.toJson(patients));

            } else if (action.equals("stats")) {
                JsonObject stats = patientService.getPatientStats();
                stats.addProperty("success", true);
                out.print(stats.toString());

            } else {
                out.print("{\"error\": \"Invalid action. Valid actions: list, getById, search, stats\"}");
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
            System.out.println("Received patient data: " + requestBody);

            if (requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Patient patient = new Patient();
            patient.setFirstName(json.get("firstName").getAsString());
            patient.setLastName(json.get("lastName").getAsString());
            patient.setDateOfBirth(LocalDate.parse(json.get("dateOfBirth").getAsString()));
            patient.setGender(json.get("gender").getAsString());
            patient.setContactNumber(json.get("contactNumber").getAsString());
            patient.setEmail(json.get("email").getAsString());
            patient.setAddress(json.get("address").getAsString());
            patient.setCity(json.get("city").getAsString());
            patient.setState(json.get("state").getAsString());
            patient.setPostalCode(json.get("postalCode").getAsString());
            patient.setNationality(json.get("nationality").getAsString());
            patient.setIdType(json.get("idType").getAsString());
            patient.setIdNumber(json.get("idNumber").getAsString());
            patient.setEmergencyContactName(json.get("emergencyContactName").getAsString());
            patient.setEmergencyContactNumber(json.get("emergencyContactNumber").getAsString());
            patient.setMedicalNotes(json.get("medicalNotes").getAsString());
            patient.setStatus("Active");

            boolean success = patientService.addPatient(patient);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Patient added successfully");
                result.addProperty("patientId", patient.getPatientId());
                result.addProperty("fullName", patient.getFullName());
                System.out.println("Patient saved to database with ID: " + patient.getPatientId());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to add patient. Please check all required fields.");
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.isEmpty()) {
                JsonObject result = new JsonObject();
                result.addProperty("success", false);
                result.addProperty("message", "Missing patient ID");
                out.print(result.toString());
                return;
            }

            int patientId = Integer.parseInt(idParam);
            boolean success = patientService.deletePatient(patientId);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Patient deleted successfully");
                System.out.println("Patient deleted: ID " + patientId);
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to delete patient");
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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}