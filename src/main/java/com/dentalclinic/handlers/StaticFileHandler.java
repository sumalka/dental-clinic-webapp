package com.dentalclinic.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {
    private static final String STATIC_DIR = "src/main/resources/static";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/") || path.isEmpty()) {
            path = "/pages/auth/login.html";
        }

        String filePath = path.startsWith("/") ? path.substring(1) : path;
        File file = new File(STATIC_DIR, filePath);

        if (!file.exists() && !path.contains(".")) {
            String htmlPath = filePath + ".html";
            File htmlFile = new File(STATIC_DIR, htmlPath);
            if (htmlFile.exists()) {
                file = htmlFile;
            }
        }

        if (file.exists()) {
            String contentType = getContentType(filePath);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            byte[] response = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        } else {
            String response = "404 - Page not found: " + path;
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}