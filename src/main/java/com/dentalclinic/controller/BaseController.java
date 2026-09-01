package com.dentalclinic.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class BaseController {
    protected final Gson gson = new GsonBuilder()
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

    protected void sendJsonResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    protected void sendSuccess(HttpServletResponse response, String message) throws IOException {
        sendJsonResponse(response, 200, new ApiResponse(true, message, null));
    }

    protected void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        sendJsonResponse(response, statusCode, new ApiResponse(false, message, null));
    }

    protected void sendSuccessWithData(HttpServletResponse response, String message, Object data) throws IOException {
        sendJsonResponse(response, 200, new ApiResponse(true, message, data));
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
    }
}