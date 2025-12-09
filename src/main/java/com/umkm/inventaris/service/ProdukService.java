package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.ProdukDao;
import com.umkm.inventaris.dto.ProdukDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Base64;

@Service
public class ProdukService {

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
        if (produkDto.getFotoProduk() != null && !produkDto.getFotoProduk().isEmpty()) {
            // Decode Base64 String dari DTO menjadi byte[]
            fotoBytes = Base64.getDecoder().decode(produkDto.getFotoProduk());
        }
        // Note: Logika ini akan selalu menimpa foto, bahkan jika string Base64 kosong.
        // Jika Anda ingin mempertahankan foto lama jika tidak ada foto baru yang
        // diunggah, diperlukan logika tambahan.
        return produkDao.update(produkDto, fotoBytes);
    }

    public int deleteProduk(Integer id) {
        return produkDao.delete(id);
    }
}