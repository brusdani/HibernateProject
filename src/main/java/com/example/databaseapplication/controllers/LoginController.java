package com.example.databaseapplication.controllers;

import com.example.databaseapplication.service.UserService;

import java.util.concurrent.ExecutorService;

public class LoginController {

    private UserService userService;

    private ExecutorService executorService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // setter for the ExecutorService
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

}
