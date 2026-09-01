package com.dentalclinic.controller;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class SessionController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject json = new JsonObject();

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            json.addProperty("authenticated", true);
            json.addProperty("username", (String) session.getAttribute("user"));
            json.addProperty("role", (String) session.getAttribute("role"));
            json.addProperty("fullName", (String) session.getAttribute("fullName"));
            json.addProperty("userId", (Integer) session.getAttribute("userId"));
        } else {
            json.addProperty("authenticated", false);
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