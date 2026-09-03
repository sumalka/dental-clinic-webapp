package com.dentalclinic.controller;

import com.dentalclinic.model.Staff;
import com.dentalclinic.model.User;
import com.dentalclinic.service.AuthService;
import com.dentalclinic.service.StaffService;
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
            System.out.println("🔐 Login request received: " + requestBody);

            if (requestBody == null || requestBody.trim().isEmpty()) {
                json.addProperty("success", false);
                json.addProperty("message", "Empty request body");
                out.print(json.toString());
                return;
            }

            JsonObject requestJson = JsonParser.parseString(requestBody).getAsJsonObject();

            if (!requestJson.has("username") || !requestJson.has("password")) {
                json.addProperty("success", false);
                json.addProperty("message", "Username and password required");
                out.print(json.toString());
                return;
            }

            String username = requestJson.get("username").getAsString();
            String password = requestJson.get("password").getAsString();

            System.out.println("🔐 Login attempt: " + username);

            // First try to authenticate as regular user
            User user = authService.authenticate(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user.getUsername());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("authType", "user");
                session.setAttribute("username", user.getUsername());

                System.out.println("✅ User login successful: " + username);
                System.out.println("✅ Session role set to: " + user.getRole());

                json.addProperty("success", true);
                json.addProperty("message", "Login successful");
                json.addProperty("username", user.getUsername());
                json.addProperty("fullName", user.getFullName());
                json.addProperty("role", user.getRole());
                json.addProperty("authType", "user");
            } else {
                // Try staff authentication
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

                    System.out.println("✅ Staff login successful: " + username);
                    System.out.println("✅ Staff ID: " + staff.getStaffId());
                    System.out.println("✅ Staff Role: " + staff.getRole());

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
                    System.out.println("❌ Login failed: " + username);
                }
            }

        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(json.toString());
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}