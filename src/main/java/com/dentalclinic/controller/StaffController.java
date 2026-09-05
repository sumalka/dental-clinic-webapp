package com.dentalclinic.controller;

import com.dentalclinic.model.Staff;
import com.dentalclinic.service.StaffService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class StaffController extends HttpServlet {
    private final Gson gson = new GsonBuilder().create();
    private final StaffService staffService = new StaffService();

    private boolean isAdmin(HttpSession session) {
        if (session == null) return false;

        String role = (String) session.getAttribute("role");
        System.out.println("Checking role from session: " + role);

        if (role != null && role.equalsIgnoreCase("admin")) {
            System.out.println("Admin role found: " + role);
            return true;
        }

        System.out.println("User is NOT admin. Role: " + role);
        return false;
    }

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
            HttpSession session = request.getSession(false);
            boolean isAdminUser = isAdmin(session);
            System.out.println("doGet - Is Admin: " + isAdminUser);

            if (action == null || action.equals("list")) {
                List<Staff> staffList = staffService.getAllStaff();

                if (!isAdminUser) {
                    for (Staff staff : staffList) {
                        staff.setPassword(null);
                    }
                }

                out.print(gson.toJson(staffList));
                System.out.println("Listed " + staffList.size() + " staff members");

            } else if (action.equals("getById")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.isEmpty()) {
                    out.print("{\"error\": \"Missing staff ID\"}");
                    return;
                }
                int id = Integer.parseInt(idParam);
                Staff staff = staffService.getStaffById(id);
                if (staff != null) {
                    if (!isAdminUser) {
                        staff.setPassword(null);
                    }
                    out.print(gson.toJson(staff));
                } else {
                    out.print("{\"error\": \"Staff not found\"}");
                }

            } else if (action.equals("getByUsername")) {
                String staffUsername = request.getParameter("username");
                if (staffUsername == null || staffUsername.isEmpty()) {
                    out.print("{\"error\": \"Missing username\"}");
                    return;
                }
                Staff staff = staffService.getStaffByUsername(staffUsername);
                if (staff != null) {
                    if (!isAdminUser) {
                        staff.setPassword(null);
                    }
                    out.print(gson.toJson(staff));
                } else {
                    out.print("{\"error\": \"Staff not found\"}");
                }

            } else if (action.equals("current")) {
                if (session != null && session.getAttribute("staffId") != null) {
                    int staffId = (Integer) session.getAttribute("staffId");
                    Staff staff = staffService.getStaffById(staffId);
                    if (staff != null) {
                        staff.setPassword(null);
                        JsonObject json = gson.toJsonTree(staff).getAsJsonObject();
                        json.addProperty("isAdmin", isAdminUser);
                        out.print(json.toString());
                    } else {
                        out.print("{\"error\": \"Staff not found\"}");
                    }
                } else {
                    out.print("{\"error\": \"Not logged in\"}");
                }

            } else {
                out.print("{\"error\": \"Invalid action. Valid: list, getById, getByUsername, current\"}");
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
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
                return;
            }

            boolean isAdminUser = isAdmin(session);
            System.out.println(" doPost - Is Admin: " + isAdminUser);

            if (!isAdminUser) {
                out.print("{\"success\": false, \"message\": \"Access denied. Admin privileges required.\"}");
                return;
            }

            String methodOverride = request.getParameter("_method");
            if ("DELETE".equalsIgnoreCase(methodOverride)) {
                handleDelete(request, response);
                return;
            }
            if ("PUT".equalsIgnoreCase(methodOverride)) {
                handlePut(request, response);
                return;
            }
            if ("PATCH".equalsIgnoreCase(methodOverride)) {
                handlePatch(request, response);
                return;
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String requestBody = sb.toString();
            System.out.println("Received staff data: " + requestBody);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Staff staff = new Staff();
            staff.setFirstName(json.get("firstName").getAsString());
            staff.setLastName(json.get("lastName").getAsString());
            staff.setUsername(json.get("username").getAsString());
            staff.setPassword(json.get("password").getAsString());
            staff.setEmail(json.get("email").getAsString());
            staff.setPhone(json.get("phone").getAsString());
            staff.setRole(json.get("role").getAsString());
            staff.setStatus("Active");

            boolean success = staffService.addStaff(staff);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Staff added successfully");
                result.addProperty("staffId", staff.getStaffId());
                result.addProperty("fullName", staff.getFullName());
                System.out.println("Staff saved with ID: " + staff.getStaffId());
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to add staff. Username may already exist.");
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

        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
                return;
            }

            if (!isAdmin(session)) {
                out.print("{\"success\": false, \"message\": \"Access denied. Admin privileges required.\"}");
                return;
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String requestBody = sb.toString();
            System.out.println("Received update staff data: " + requestBody);

            if (requestBody.trim().isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Empty request body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            Staff staff = new Staff();
            staff.setStaffId(json.get("staffId").getAsInt());
            staff.setFirstName(json.get("firstName").getAsString());
            staff.setLastName(json.get("lastName").getAsString());
            staff.setUsername(json.get("username").getAsString());

            if (json.has("password") && !json.get("password").getAsString().isEmpty()) {
                staff.setPassword(json.get("password").getAsString());
            } else {
                Staff existing = staffService.getStaffById(staff.getStaffId());
                if (existing != null) {
                    staff.setPassword(existing.getPassword());
                }
            }

            staff.setEmail(json.get("email").getAsString());
            staff.setPhone(json.get("phone").getAsString());
            staff.setRole(json.get("role").getAsString());

            if (json.has("status") && !json.get("status").isJsonNull()) {
                staff.setStatus(json.get("status").getAsString());
            }

            boolean success = staffService.updateStaff(staff);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Staff updated successfully");
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to update staff");
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

        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
                return;
            }

            if (!isAdmin(session)) {
                out.print("{\"success\": false, \"message\": \"Access denied. Admin privileges required.\"}");
                return;
            }

            if (idParam == null || idParam.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Missing staff ID\"}");
                return;
            }

            int staffId = Integer.parseInt(idParam);

            Integer currentStaffId = (Integer) session.getAttribute("staffId");
            if (currentStaffId != null && currentStaffId == staffId) {
                out.print("{\"success\": false, \"message\": \"Cannot delete your own account\"}");
                return;
            }

            boolean success = staffService.deleteStaff(staffId);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Staff deleted successfully");
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to delete staff");
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

        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                out.print("{\"success\": false, \"message\": \"Not authenticated\"}");
                return;
            }

            if (!isAdmin(session)) {
                out.print("{\"success\": false, \"message\": \"Access denied. Admin privileges required.\"}");
                return;
            }

            if (idParam == null || idParam.isEmpty()) {
                out.print("{\"success\": false, \"message\": \"Missing staff ID\"}");
                return;
            }

            int staffId = Integer.parseInt(idParam);

            Integer currentStaffId = (Integer) session.getAttribute("staffId");
            if (currentStaffId != null && currentStaffId == staffId) {
                out.print("{\"success\": false, \"message\": \"Cannot change your own status\"}");
                return;
            }

            boolean success = staffService.toggleStaffStatus(staffId);

            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "Staff status toggled successfully");
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to toggle staff status");
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