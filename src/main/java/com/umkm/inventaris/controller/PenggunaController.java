package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.PenggunaDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.service.PenggunaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pengguna")
@CrossOrigin("*")
public class PenggunaController {

    private static final Logger logger = LoggerFactory.getLogger(PenggunaController.class);

    @Autowired
    private PenggunaService penggunaService;

    @GetMapping
    public ResponseEntity<List<PenggunaDto>> getAllPengguna() {
        try {
            return ResponseEntity.ok(penggunaService.listAllPengguna());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Pengguna: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPenggunaById(@PathVariable Integer id) {
        try {
            PenggunaDto pengguna = penggunaService.getPenggunaById(id);
            return ResponseEntity.ok(pengguna);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saat mencari Pengguna dengan id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPengguna(@RequestBody PenggunaDto penggunaDto) {
        try {
            if (penggunaService.createPengguna(penggunaDto) > 0) {
                return new ResponseEntity<>("Data Pengguna berhasil dibuat.", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Gagal membuat data Pengguna.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error saat membuat data Pengguna: {}", e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePengguna(@PathVariable Integer id, @RequestBody PenggunaDto penggunaDto) {
        try {
            penggunaDto.setId(id);
            if (penggunaService.updatePengguna(penggunaDto) > 0) {
                return ResponseEntity.ok("Data Pengguna berhasil diperbarui.");
            }
            return new ResponseEntity<>("Gagal memperbarui, Pengguna dengan id " + id + " tidak ditemukan.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error saat memperbarui data Pengguna dengan id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePengguna(@PathVariable Integer id) {
        if (penggunaService.deletePengguna(id) > 0) {
            return ResponseEntity.ok("Data Pengguna berhasil dihapus.");
        }
        return new ResponseEntity<>("Gagal menghapus, Pengguna dengan id " + id + " tidak ditemukan.",
                HttpStatus.NOT_FOUND);
    }
}