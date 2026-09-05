package com.dentalclinic.controller;

import com.dentalclinic.model.Staff;
import com.dentalclinic.model.User;
import com.dentalclinic.service.AuthService;
import com.dentalclinic.service.StaffService;
import com.dentalclinic.utils.EmailUtil;
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

public class AuthController extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final StaffService staffService = new StaffService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        JsonObject json = new JsonObject();

        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");
            String role = (String) session.getAttribute("role");
            Integer staffId = (Integer) session.getAttribute("staffId");

            if (username != null || staffId != null) {
                json.addProperty("authenticated", true);
                json.addProperty("username", username != null ? username : "");
                json.addProperty("role", role != null ? role : "");
                json.addProperty("staffId", staffId != null ? staffId : 0);
                json.addProperty("isAdmin", role != null && role.equalsIgnoreCase("admin"));
            } else {
                json.addProperty("authenticated", false);
            }
        } else {
            json.addProperty("authenticated", false);
        }

        out.print(json.toString());
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
        JsonObject json = new JsonObject();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String requestBody = sb.toString();
            System.out.println("Auth request received: " + requestBody);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                json.addProperty("success", false);
                json.addProperty("message", "Empty request body");
                out.print(json.toString());
                return;
            }

            JsonObject requestJson = JsonParser.parseString(requestBody).getAsJsonObject();

            // ===== CHECK FOR GOOGLE RECOVERY =====
            if (requestJson.has("credential")) {
                String credential = requestJson.get("credential").getAsString();
                System.out.println("Google recovery attempt received");

                String staffEmail = extractEmailFromToken(credential);
                System.out.println("Extracted email from token: '" + staffEmail + "'");

                if (staffEmail == null || staffEmail.isEmpty()) {
                    JsonObject result = new JsonObject();
                    result.addProperty("success", false);
                    result.addProperty("isStaff", false);
                    result.addProperty("message", "Unable to extract email from Google account");
                    out.print(result.toString());
                    return;
                }

                Staff staff = findStaffByEmail(staffEmail);
                System.out.println("Staff found: " + (staff != null ? staff.getUsername() : "null"));

                if (staff == null) {
                    JsonObject result = new JsonObject();
                    result.addProperty("success", false);
                    result.addProperty("isStaff", false);
                    result.addProperty("message", "No staff account found with email: " + staffEmail);
                    out.print(result.toString());
                    return;
                }

                // Generate temporary password (alphanumeric only to avoid encoding issues)
                String tempPassword = generateTemporaryPassword();
                System.out.println("Generated temporary password for: " + staff.getUsername());

                boolean passwordUpdated = updateStaffPassword(staff.getStaffId(), tempPassword);
                System.out.println("Password updated in database: " + passwordUpdated);

                if (!passwordUpdated) {
                    JsonObject result = new JsonObject();
                    result.addProperty("success", false);
                    result.addProperty("isStaff", false);
                    result.addProperty("message", "Failed to update password. Please try again.");
                    out.print(result.toString());
                    return;
                }

                boolean emailSent = EmailUtil.sendPasswordRecoveryEmail(
                        staffEmail,
                        staff.getUsername(),
                        tempPassword
                );

                JsonObject result = new JsonObject();
                if (emailSent) {
                    result.addProperty("success", true);
                    result.addProperty("isStaff", true);
                    result.addProperty("message", "Recovery email sent successfully to " + staffEmail);
                    System.out.println("Recovery email sent to: " + staffEmail);
                } else {
                    result.addProperty("success", false);
                    result.addProperty("isStaff", false);
                    result.addProperty("message", "Failed to send recovery email. Please try again.");
                    System.out.println("Failed to send recovery email to: " + staffEmail);
                }
                out.print(result.toString());
                return;
            }

            // ===== REGULAR LOGIN =====
            if (!requestJson.has("username") || !requestJson.has("password")) {
                json.addProperty("success", false);
                json.addProperty("message", "Username and password required");
                out.print(json.toString());
                return;
            }

            String username = requestJson.get("username").getAsString();
            String password = requestJson.get("password").getAsString();

            System.out.println("Login attempt: " + username);

            User user = authService.authenticate(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user.getUsername());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("authType", "user");
                session.setAttribute("username", user.getUsername());

                System.out.println("User login successful: " + username);
                System.out.println("Session role set to: " + user.getRole());

                json.addProperty("success", true);
                json.addProperty("message", "Login successful");
                json.addProperty("username", user.getUsername());
                json.addProperty("fullName", user.getFullName());
                json.addProperty("role", user.getRole());
                json.addProperty("authType", "user");
            } else {
                Staff staff = staffService.authenticate(username, password);

                if (staff != null) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("staffId", staff.getStaffId());
                    session.setAttribute("username", staff.getUsername());
                    session.setAttribute("fullName", staff.getFullName());
                    session.setAttribute("role", staff.getRole());
                    session.setAttribute("authType", "staff");
                    session.setAttribute("user", staff.getUsername());
                    session.setAttribute("userId", staff.getStaffId());

                    System.out.println("Staff login successful: " + username);
                    System.out.println("Staff ID: " + staff.getStaffId());
                    System.out.println("Staff Role: " + staff.getRole());

                    json.addProperty("success", true);
                    json.addProperty("message", "Login successful");
                    json.addProperty("staffId", staff.getStaffId());
                    json.addProperty("username", staff.getUsername());
                    json.addProperty("fullName", staff.getFullName());
                    json.addProperty("role", staff.getRole());
                    json.addProperty("authType", "staff");
                } else {
                    json.addProperty("success", false);
                    json.addProperty("message", "Invalid username or password");
                    System.out.println("Login failed: " + username);
                }
            }

        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", "An unexpected error occurred. Please try again.");
            e.printStackTrace();
        }

        out.print(json.toString());
    }

    private String extractEmailFromToken(String credential) {
        try {
            String[] parts = credential.split("\\.");
            if (parts.length < 2) {
                return null;
            }

            String payload = parts[1];
            while (payload.length() % 4 != 0) {
                payload = payload + "=";
            }

            byte[] decoded = java.util.Base64.getUrlDecoder().decode(payload);
            String jsonPayload = new String(decoded, "UTF-8");

            JsonObject json = JsonParser.parseString(jsonPayload).getAsJsonObject();

            if (json.has("email")) {
                return json.get("email").getAsString();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error extracting email from token: " + e.getMessage());
            return null;
        }
    }

    private Staff findStaffByEmail(String email) {
        try {
            System.out.println("Searching for staff with email: '" + email + "'");
            List<Staff> allStaff = staffService.getAllStaff();
            System.out.println("Total staff loaded: " + allStaff.size());

            for (Staff staff : allStaff) {
                String staffEmail = staff.getEmail();
                System.out.println("Checking staff: " + staff.getUsername() + " | Email: '" + staffEmail + "'");

                if (staffEmail != null) {
                    if (staffEmail.trim().equalsIgnoreCase(email.trim())) {
                        System.out.println("FOUND matching staff: " + staff.getUsername());
                        return staff;
                    }
                }
            }

            System.out.println("No staff found with email: " + email);
            return null;
        } catch (Exception e) {
            System.err.println("Error finding staff by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String generateTemporaryPassword() {
        // Use only alphanumeric characters to avoid URL encoding issues
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    private boolean updateStaffPassword(int staffId, String newPassword) {
        try {
            Staff staff = staffService.getStaffById(staffId);
            if (staff == null) {
                System.err.println("Staff not found with ID: " + staffId);
                return false;
            }
            staff.setPassword(newPassword);
            return staffService.updateStaff(staff);
        } catch (Exception e) {
            System.err.println("Error updating staff password: " + e.getMessage());
            e.printStackTrace();
            return false;
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