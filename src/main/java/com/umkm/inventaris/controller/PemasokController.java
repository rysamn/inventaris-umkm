package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.PemasokDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.service.PemasokService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pemasok")
@CrossOrigin("*")
public class PemasokController {

    private static final Logger logger = LoggerFactory.getLogger(PemasokController.class);

    @Autowired
    private PemasokService pemasokService;

    @GetMapping
    public ResponseEntity<List<PemasokDto>> getAllPemasok() {
        try {
            return ResponseEntity.ok(pemasokService.listAllPemasok());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Pemasok: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPemasokById(@PathVariable Integer id) {
        try {
            PemasokDto pemasok = pemasokService.getPemasokById(id);
            return ResponseEntity.ok(pemasok);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saat mencari Pemasok dengan id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPemasok(@RequestBody PemasokDto pemasokDto) {
        try {
            if (pemasokService.createPemasok(pemasokDto) > 0) {
                return new ResponseEntity<>("Data Pemasok berhasil dibuat.", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Gagal membuat data Pemasok.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error saat membuat data Pemasok: {}", e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePemasok(@PathVariable Integer id, @RequestBody PemasokDto pemasokDto) {
        try {
            pemasokDto.setId(id);
            if (pemasokService.updatePemasok(pemasokDto) > 0) {
                return ResponseEntity.ok("Data Pemasok berhasil diperbarui.");
            }
            return new ResponseEntity<>("Gagal memperbarui, Pemasok dengan id " + id + " tidak ditemukan.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error saat memperbarui data Pemasok dengan id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePemasok(@PathVariable Integer id) {
        if (pemasokService.deletePemasok(id) > 0) {
            return ResponseEntity.ok("Data Pemasok berhasil dihapus.");
        }
        return new ResponseEntity<>("Gagal menghapus, Pemasok dengan id " + id + " tidak ditemukan.",
                HttpStatus.NOT_FOUND);
    }
}