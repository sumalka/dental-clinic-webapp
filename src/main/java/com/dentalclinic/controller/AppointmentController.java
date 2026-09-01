package com.dentalclinic.controller;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.service.AppointmentService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentController extends HttpServlet {
    private final AppointmentService appointmentService = new AppointmentService();

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
                List<Appointment> appointments = appointmentService.getAllAppointments();
                String json = convertToJson(appointments);
                out.print(json);
                System.out.println("Listed " + appointments.size() + " appointments");

            } else if (action.equals("getById")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isEmpty()) {
                    out.print("{\"error\": \"Missing appointment ID\"}");
                    return;
                }
                int id = Integer.parseInt(idParam);
                Appointment appointment = appointmentService.getAppointmentById(id);
                if (appointment != null) {
                    out.print(convertToJson(appointment));
                } else {
                    out.print("{\"error\": \"Appointment not found\"}");
                }

            } else if (action.equals("getByNumber")) {
                String number = request.getParameter("number");
                Appointment appointment = appointmentService.getAppointmentByNumber(number);
                if (appointment != null) {
                    out.print(convertToJson(appointment));
                } else {
                    out.print("{\"error\": \"Appointment not found\"}");
                }

            } else if (action.equals("today")) {
                int count = appointmentService.getTodayAppointmentCount();
                out.print("{\"success\": true, \"count\": " + count + "}");

            } else if (action.equals("byPatient")) {
                String patientIdParam = request.getParameter("patientId");
                if (patientIdParam == null || patientIdParam.isEmpty()) {
                    out.print("{\"error\": \"Missing patient ID\"}");
                    return;
                }
                int patientId = Integer.parseInt(patientIdParam);
                List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
                out.print(convertToJson(appointments));

            } else {
                out.print("{\"error\": \"Invalid action. Valid: list, getById, getByNumber, today, byPatient\"}");
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
            System.out.println("Received appointment data: " + requestBody);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            // Check for action field first
            if (json.has("action")) {
                String action = json.get("action").getAsString();

                // Handle status update (confirm, complete, cancel)
                if ("update".equals(action)) {
                    // Check if this is a simple status update or full update
                    if (json.has("appointmentId") && json.has("status") &&
                            !json.has("patientId") && !json.has("dentistId")) {

                        // Simple status update
                        int appointmentId = json.get("appointmentId").getAsInt();
                        String status = json.get("status").getAsString();

                        boolean success = appointmentService.updateAppointmentStatus(appointmentId, status);

                        JsonObject result = new JsonObject();
                        if (success) {
                            result.addProperty("success", true);
                            result.addProperty("message", "Appointment " + status.toLowerCase() + " successfully");
                            System.out.println("Appointment status updated: ID " + appointmentId + " -> " + status);
                        } else {
                            result.addProperty("success", false);
                            result.addProperty("message", "Failed to update appointment status");
                        }
                        out.print(result.toString());
                        return;
                    }

                    // Full update with all fields (from edit form)
                    if (json.has("appointmentId") && json.has("patientId") && json.has("dentistId")) {
                        int appointmentId = json.get("appointmentId").getAsInt();
                        int patientId = json.get("patientId").getAsInt();
                        int dentistId = json.get("dentistId").getAsInt();
                        int treatmentId = json.get("treatmentId").getAsInt();
                        String dateStr = json.get("appointmentDate").getAsString();
                        String timeStr = json.get("appointmentTime").getAsString();
                        int duration = json.get("durationMinutes").getAsInt();
                        String status = json.get("status").getAsString();
                        String notes = json.has("notes") ? json.get("notes").getAsString() : "";

                        Appointment appointment = new Appointment();
                        appointment.setAppointmentId(appointmentId);
                        appointment.setPatientId(patientId);
                        appointment.setDentistId(dentistId);
                        appointment.setTreatmentId(treatmentId);
                        appointment.setAppointmentDate(LocalDate.parse(dateStr));
                        appointment.setAppointmentTime(LocalTime.parse(timeStr));
                        appointment.setDurationMinutes(duration);
                        appointment.setStatus(status);
                        appointment.setNotes(notes);

                        boolean success = appointmentService.updateAppointment(appointment);

                        JsonObject result = new JsonObject();
                        if (success) {
                            result.addProperty("success", true);
                            result.addProperty("message", "Appointment updated successfully");
                            System.out.println("Appointment updated: ID " + appointmentId);
                        } else {
                            result.addProperty("success", false);
                            result.addProperty("message", "Failed to update appointment");
                        }
                        out.print(result.toString());
                        return;
                    }
                }
            }

            // Create new appointment (original logic)
            Appointment appointment = new Appointment();
            appointment.setPatientId(json.get("patientId").getAsInt());
            appointment.setDentistId(json.get("dentistId").getAsInt());

            if (json.has("treatmentId") && !json.get("treatmentId").isJsonNull()) {
                appointment.setTreatmentId(json.get("treatmentId").getAsInt());
            }

            String dateStr = json.get("appointmentDate").getAsString();
            appointment.setAppointmentDate(LocalDate.parse(dateStr));

            String timeStr = json.get("appointmentTime").getAsString();
            appointment.setAppointmentTime(LocalTime.parse(timeStr));

            if (json.has("durationMinutes")) {
                appointment.setDurationMinutes(json.get("durationMinutes").getAsInt());
            } else {
                appointment.setDurationMinutes(30);
            }

            if (json.has("notes") && !json.get("notes").isJsonNull()) {
                appointment.setNotes(json.get("notes").getAsString());
            }

            if (json.has("createdBy") && !json.get("createdBy").isJsonNull()) {
                appointment.setCreatedBy(json.get("createdBy").getAsInt());
            } else {
                appointment.setCreatedBy(1);
            }

            boolean success = appointmentService.createAppointment(appointment);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Appointment created successfully");
                result.addProperty("appointmentId", appointment.getAppointmentId());
                result.addProperty("appointmentNumber", appointment.getAppointmentNumber());
                System.out.println("Appointment saved with ID: " + appointment.getAppointmentId());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to create appointment. Please check all required fields.");
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

    private String convertToJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(convertToJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        if (obj instanceof Appointment) {
            Appointment a = (Appointment) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"appointmentId\":").append(a.getAppointmentId()).append(",");
            sb.append("\"appointmentNumber\":\"").append(escapeJson(a.getAppointmentNumber())).append("\",");
            sb.append("\"patientId\":").append(a.getPatientId()).append(",");
            sb.append("\"dentistId\":").append(a.getDentistId()).append(",");
            sb.append("\"treatmentId\":").append(a.getTreatmentId() != null ? a.getTreatmentId() : "null").append(",");
            sb.append("\"appointmentDate\":\"").append(a.getAppointmentDate()).append("\",");
            sb.append("\"appointmentTime\":\"").append(a.getAppointmentTime()).append("\",");
            sb.append("\"durationMinutes\":").append(a.getDurationMinutes()).append(",");
            sb.append("\"status\":\"").append(a.getStatus()).append("\",");
            sb.append("\"notes\":\"").append(escapeJson(a.getNotes())).append("\",");
            sb.append("\"patientName\":\"").append(escapeJson(a.getPatientName())).append("\",");
            sb.append("\"dentistName\":\"").append(escapeJson(a.getDentistName())).append("\",");
            sb.append("\"treatmentName\":\"").append(escapeJson(a.getTreatmentName())).append("\"");
            sb.append("}");
            return sb.toString();
        }

        return "null";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}