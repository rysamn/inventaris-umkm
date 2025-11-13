package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.PelangganDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.service.PelangganService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pelanggan")
@CrossOrigin("*")
public class PelangganController {

    private static final Logger logger = LoggerFactory.getLogger(PelangganController.class);

    @Autowired
    private PelangganService pelangganService;

    @GetMapping
    public ResponseEntity<List<PelangganDto>> getAllPelanggan() {
        try {
            return ResponseEntity.ok(pelangganService.listAllPelanggan());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Pelanggan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPelangganById(@PathVariable Integer id) {
        try {
            PelangganDto pelanggan = pelangganService.getPelangganById(id);
            return ResponseEntity.ok(pelanggan);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saat mencari Pelanggan dengan id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPelanggan(@RequestBody PelangganDto pelangganDto) {
        try {
            if (pelangganService.createPelanggan(pelangganDto) > 0) {
                return new ResponseEntity<>("Data Pelanggan berhasil dibuat.", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Gagal membuat data Pelanggan.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error saat membuat data Pelanggan: {}", e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePelanggan(@PathVariable Integer id, @RequestBody PelangganDto pelangganDto) {
        try {
            pelangganDto.setId(id);
            if (pelangganService.updatePelanggan(pelangganDto) > 0) {
                return ResponseEntity.ok("Data Pelanggan berhasil diperbarui.");
            }
            return new ResponseEntity<>("Gagal memperbarui, Pelanggan dengan id " + id + " tidak ditemukan.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error saat memperbarui data Pelanggan dengan id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePelanggan(@PathVariable Integer id) {
        if (pelangganService.deletePelanggan(id) > 0) {
            return ResponseEntity.ok("Data Pelanggan berhasil dihapus.");
        }
        return new ResponseEntity<>("Gagal menghapus, Pelanggan dengan id " + id + " tidak ditemukan.",
                HttpStatus.NOT_FOUND);
    }
}