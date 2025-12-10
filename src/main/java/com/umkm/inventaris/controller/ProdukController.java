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

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/produk")
@CrossOrigin("*")
public class ProdukController {

    private static final Logger logger = LoggerFactory.getLogger(ProdukController.class);

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

    @GetMapping("/without-foto")
    public ResponseEntity<List<ProdukDto>> getAllProdukWithoutFoto() {
        try {
            return ResponseEntity.ok(produkService.listAllProdukWithoutFoto());
        } catch (Exception e) {
            logger.error("Error saat mengambil list data Produk: {}", e.getMessage(), e);
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

            // Jika foto mengandung prefix data URL (tanda foto lama dari frontend), set
            // null
            // Sehingga ProdukService akan skip update foto
            if (produkDto.getFotoProduk() != null &&
                    produkDto.getFotoProduk().startsWith("data:image/") &&
                    !isBase64DataUrl(produkDto.getFotoProduk())) {
                produkDto.setFotoProduk(null); // Skip foto update jika hanya foto lama
            }

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

    private boolean isBase64DataUrl(String str) {
        // Cek apakah string adalah Base64 yang valid (bukan hanya data URL dengan foto
        // lama)
        if (!str.startsWith("data:image/"))
            return false;
        int commaIndex = str.indexOf(",");
        if (commaIndex == -1)
            return false;
        try {
            String base64Part = str.substring(commaIndex + 1);
            Base64.getDecoder().decode(base64Part);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
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
}