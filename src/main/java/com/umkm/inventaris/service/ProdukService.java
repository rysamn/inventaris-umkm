package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.ProdukDao;
import com.umkm.inventaris.dto.ProdukDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Base64;

@Service
public class ProdukService {

    private static final Logger logger = LoggerFactory.getLogger(ProdukService.class);

    @Autowired
    private ProdukDao produkDao;

    public List<ProdukDto> listAllProduk() {
        return produkDao.findAll();
    }

    public List<ProdukDto> listAllProdukWithoutFoto() {
        return produkDao.findAllWithoutFoto();
    }

    public ProdukDto getProdukById(Integer id) {
        return produkDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk", "id", id));
    }

    public int createProduk(ProdukDto produkDto) {
        byte[] fotoBytes = null;
        if (produkDto.getFotoProduk() != null && !produkDto.getFotoProduk().isEmpty()) {
            // Decode Base64 String dari DTO menjadi byte[]
            fotoBytes = Base64.getDecoder().decode(produkDto.getFotoProduk());
        }
        return produkDao.save(produkDto, fotoBytes);
    }

    public int updateProduk(ProdukDto produkDto) {
        byte[] fotoBytes = null;

        // Hanya decode dan update foto jika ada foto baru yang dikirim
        if (produkDto.getFotoProduk() != null && !produkDto.getFotoProduk().isEmpty()) {
            String foto = produkDto.getFotoProduk();

            // Jika foto berisi prefix "data:image", ekstrak bagian Base64 saja
            if (foto.startsWith("data:image/")) {
                foto = foto.substring(foto.indexOf(",") + 1);
            }

            try {
                fotoBytes = Base64.getDecoder().decode(foto);
            } catch (IllegalArgumentException e) {
                // Jika decode gagal, kemungkinan data corrupted
                logger.warn("Invalid Base64 data for foto, skipping foto update");
                fotoBytes = null; // Skip foto update
            }
        }
        // Jika fotoBytes null, DAO akan preserve foto lama

        return produkDao.update(produkDto, fotoBytes);
    }

    public int deleteProduk(Integer id) {
        return produkDao.delete(id);
    }
}