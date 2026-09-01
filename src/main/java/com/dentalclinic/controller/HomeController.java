package com.dentalclinic.controller;

import com.dentalclinic.model.User;
import com.dentalclinic.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;

public class HomeController extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("🔍 HomeController request: " + path);

        // API requests - DO NOT INTERCEPT
        if (path.startsWith("/api/")) {
            return;
        }

        HttpSession session = request.getSession(false);

        // Handle logout
        if ("/logout".equals(path)) {
            if (session != null) {
                session.invalidate();
                System.out.println("✅ Logout successful");
            }
            response.sendRedirect(request.getContextPath() + "/pages/auth/login.html");
            return;
        }

        // Handle login page
        if ("/login".equals(path)) {
            if (session != null && session.getAttribute("user") != null) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            servePage("/pages/auth/login.html", request, response);
            return;
        }

        // Handle dashboard
        if ("/dashboard".equals(path) || "/".equals(path)) {
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            servePage("/pages/dashboard/index.html", request, response);
            return;
        }

        // Handle module pages - requires authentication
        // 🔥 FIXED: Added all module mappings including billing, reports, help
        String[] modules = {"/patients", "/appointments", "/dentists", "/treatments",
                "/billing", "/reports", "/staff", "/help"};
        for (String module : modules) {
            if (path.equals(module)) {
                if (session == null || session.getAttribute("user") == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                // 🔥 FIXED: Use proper page mapping for each module
                String pagePath = getPagePathForModule(module);
                servePage(pagePath, request, response);
                return;
            }
        }

        // Handle static resources
        if (path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".html")) {
            serveResource(path, request, response);
            return;
        }

        // Default - redirect to login
        response.sendRedirect(request.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("🔐 POST request: " + path);

        // API requests - DO NOT INTERCEPT
        if (path.startsWith("/api/")) {
            return;
        }

        // Handle login form submission
        if ("/login".equals(path)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            System.out.println("🔐 Login attempt: " + username);

            String requestedWith = request.getHeader("X-Requested-With");
            boolean isAjax = "XMLHttpRequest".equals(requestedWith);

            User user = authService.authenticate(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user.getUsername());
                session.setAttribute("fullName", user.getFullName());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userId", user.getUserId());

                System.out.println("✅ Login successful: " + username);

                if (isAjax) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().print("{\"success\": true, \"redirect\": \"" +
                            request.getContextPath() + "/dashboard\"}");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
            } else {
                System.out.println("❌ Login failed: " + username);
                if (isAjax) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().print("{\"success\": false, \"error\": \"Invalid credentials\"}");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login?error=true");
                }
            }
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Maps module paths to their corresponding HTML files
     */
    private String getPagePathForModule(String module) {
        switch (module) {
            case "/patients": return "/pages/patients/list.html";
            case "/appointments": return "/pages/appointments/list.html";
            case "/dentists": return "/pages/dentists/list.html";
            case "/treatments": return "/pages/treatments/list.html";
            case "/billing": return "/pages/billing/invoice.html";
            case "/reports": return "/pages/reports/daily.html";
            case "/staff": return "/pages/staff/list.html";
            case "/help": return "/pages/help/index.html";
            default: return "/pages/dashboard/index.html";
        }
    }

    private void servePage(String pagePath, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (InputStream in = getServletContext().getResourceAsStream(pagePath)) {
            if (in != null) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
                System.out.println("✅ Served: " + pagePath);
            } else {
                System.out.println("❌ Page not found: " + pagePath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("❌ Error serving page: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void serveResource(String resourcePath, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        if (resourcePath.endsWith(".css")) response.setContentType("text/css");
        else if (resourcePath.endsWith(".js")) response.setContentType("application/javascript");
        else if (resourcePath.endsWith(".png")) response.setContentType("image/png");
        else if (resourcePath.endsWith(".jpg") || resourcePath.endsWith(".jpeg")) response.setContentType("image/jpeg");
        else if (resourcePath.endsWith(".html")) response.setContentType("text/html;charset=UTF-8");

        try (InputStream in = getServletContext().getResourceAsStream("/" + resourcePath)) {
            if (in != null) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}