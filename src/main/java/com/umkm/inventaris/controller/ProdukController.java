package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.ProdukDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.service.ProdukService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produk")
@CrossOrigin("*")
public class ProdukController {

    private static final Logger logger = LoggerFactory.getLogger(ProdukController.class);
    private static final String UPLOAD_DIR = "uploads/produk/";

    @Autowired
    private ProdukService produkService;

    @GetMapping
    public ResponseEntity<List<ProdukDto>> getAllProduk() {
        try {
            return ResponseEntity.ok(produkService.listAllProduk());
        } catch (Exception e) {
            logger.error("Error saat mengambil semua data Produk: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProdukById(@PathVariable Integer id) {
        try {
            ProdukDto produk = produkService.getProdukById(id);
            return ResponseEntity.ok(produk);
        } catch (ResourceNotFoundException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saat mencari Produk dengan id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createProduk(@RequestBody ProdukDto produkDto) {
        try {
            if (produkService.createProduk(produkDto) > 0) {
                return new ResponseEntity<>("Data Produk berhasil dibuat.", HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Gagal membuat data Produk.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error saat membuat data Produk: {}", e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduk(@PathVariable Integer id, @RequestBody ProdukDto produkDto) {
        try {
            produkDto.setId(id);
            if (produkService.updateProduk(produkDto) > 0) {
                return ResponseEntity.ok("Data Produk berhasil diperbarui.");
            }
            return new ResponseEntity<>("Gagal memperbarui, Produk dengan id " + id + " tidak ditemukan.",
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error saat memperbarui data Produk dengan id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Terjadi kesalahan pada server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduk(@PathVariable Integer id) {
        if (produkService.deleteProduk(id) > 0) {
            return ResponseEntity.ok("Data Produk berhasil dihapus.");
        }
        return new ResponseEntity<>("Gagal menghapus, Produk dengan id " + id + " tidak ditemukan.",
                HttpStatus.NOT_FOUND);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFoto(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("File tidak boleh kosong.", HttpStatus.BAD_REQUEST);
            }
            
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Files.write(Paths.get(UPLOAD_DIR + fileName), file.getBytes());
            
            return ResponseEntity.ok(fileName);
        } catch (IOException e) {
            logger.error("Error saat upload foto: {}", e.getMessage(), e);
            return new ResponseEntity<>("Gagal upload foto.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}