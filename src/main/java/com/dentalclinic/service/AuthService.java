package com.dentalclinic.service;

import com.dentalclinic.dao.UserDAO;
import com.dentalclinic.model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.authenticate(username, password);
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
}