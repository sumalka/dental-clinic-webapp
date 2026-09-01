package com.dentalclinic.controller;

import com.dentalclinic.model.User;
import com.dentalclinic.service.AuthService;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject json = new JsonObject();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JsonObject requestJson = JsonParser.parseString(sb.toString()).getAsJsonObject();
            String username = requestJson.get("username").getAsString();
            String password = requestJson.get("password").getAsString();

            User user = authService.authenticate(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user.getUsername());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userId", user.getUserId());

                json.addProperty("success", true);
                json.addProperty("message", "Login successful");
                json.addProperty("username", user.getUsername());
                json.addProperty("fullName", user.getFullName());
                json.addProperty("role", user.getRole());
            } else {
                json.addProperty("success", false);
                json.addProperty("message", "Invalid username or password");
            }

        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", "Error: " + e.getMessage());
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