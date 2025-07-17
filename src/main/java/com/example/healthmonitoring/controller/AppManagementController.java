package com.example.healthmonitoring.controller;

import com.example.healthmonitoring.dto.app.AppConfigResponse;
import com.example.healthmonitoring.service.AppManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apps")
public class AppManagementController {

    @Autowired
    private AppManagementService appManagementService;

    @GetMapping
    public List<AppConfigResponse> getAllApps() {
        return appManagementService.getAllApps();
    }
}
