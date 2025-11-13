package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.DashboardDto;
import com.umkm.inventaris.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        try {
            DashboardDto dashboard = dashboardService.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error saat mengambil data Dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Terjadi kesalahan saat mengambil data dashboard");
        }
    }
}