package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.PembelianDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.service.PembelianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/pembelian")
@CrossOrigin("*")
public class PembelianController {

    private static final Logger logger = LoggerFactory.getLogger(PembelianController.class);

    @Autowired
    private PembelianService pembelianService;

    @GetMapping
    public ResponseEntity<List<PembelianDto>> getAllPembelian() {
        try {
            return ResponseEntity.ok(pembelianService.listAllPembelian());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Pembelian: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPembelianById(@PathVariable Integer id) {
        try {
            PembelianDto pembelian = pembelianService.getPembelianById(id);
            return ResponseEntity.ok(pembelian);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saat mencari Pembelian dengan id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPembelian(@RequestBody PembelianDto pembelianDto) {
        try {
            if (pembelianService.createPembelian(pembelianDto) > 0) {
                return new ResponseEntity<>("Transaksi Pembelian berhasil dibuat dan stok produk telah diperbarui.", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Gagal membuat Pembelian.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error saat membuat Pembelian: {}", e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePembelian(@PathVariable Integer id) {
        try {
            if (pembelianService.deletePembelian(id) > 0) {
                return ResponseEntity.ok("Pembelian berhasil dihapus dan stok produk telah dikembalikan.");
            }
            return new ResponseEntity<>("Gagal menghapus, Pembelian dengan id " + id + " tidak ditemukan.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error saat menghapus Pembelian dengan id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}