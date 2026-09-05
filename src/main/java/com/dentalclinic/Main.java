package com.dentalclinic;

import com.dentalclinic.handlers.StaticFileHandler;
import com.dentalclinic.utils.DatabaseConnection;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Initialize database
        DatabaseConnection.getInstance();

        System.out.println("\n========================================");
        System.out.println("Dental Clinic Management System");
        System.out.println("========================================");

        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started on: http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop");
        System.out.println("========================================\n");
    }
}