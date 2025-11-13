package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.LogInventarisDto;
import com.umkm.inventaris.service.LogInventarisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/log")
@CrossOrigin("*")
public class LogInventarisController {

    private static final Logger logger = LoggerFactory.getLogger(LogInventarisController.class);

    @Autowired
    private LogInventarisService logInventarisService;

    @GetMapping
    public ResponseEntity<List<LogInventarisDto>> getAllLog() {
        try {
            return ResponseEntity.ok(logInventarisService.listAllLog());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Log: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<List<LogInventarisDto>> getLatestLog(@RequestParam(defaultValue = "10") int limit) {
        try {
            return ResponseEntity.ok(logInventarisService.listLatestLog(limit));
        } catch (Exception e) {
            logger.error("Error saat mengambil log terbaru: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}