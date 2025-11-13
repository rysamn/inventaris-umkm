package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.PenjualanDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.service.PenjualanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/penjualan")
@CrossOrigin("*")
public class PenjualanController {

    private static final Logger logger = LoggerFactory.getLogger(PenjualanController.class);

    @Autowired
    private PenjualanService penjualanService;

    @GetMapping
    public ResponseEntity<List<PenjualanDto>> getAllPenjualan() {
        try {
            return ResponseEntity.ok(penjualanService.listAllPenjualan());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Penjualan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPenjualanById(@PathVariable Integer id) {
        try {
            PenjualanDto penjualan = penjualanService.getPenjualanById(id);
            return ResponseEntity.ok(penjualan);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saat mencari Penjualan dengan id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPenjualan(@RequestBody PenjualanDto penjualanDto) {
        try {
            if (penjualanService.createPenjualan(penjualanDto) > 0) {
                return new ResponseEntity<>("Transaksi Penjualan berhasil dibuat dan stok produk telah diperbarui.", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Gagal membuat Penjualan.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error saat membuat Penjualan: {}", e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePenjualan(@PathVariable Integer id) {
        try {
            if (penjualanService.deletePenjualan(id) > 0) {
                return ResponseEntity.ok("Penjualan berhasil dihapus dan stok produk telah dikembalikan.");
            }
            return new ResponseEntity<>("Gagal menghapus, Penjualan dengan id " + id + " tidak ditemukan.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error saat menghapus Penjualan dengan id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}