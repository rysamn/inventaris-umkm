package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.ProdukDao;
import com.umkm.inventaris.dto.ProdukDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdukService {

    @Autowired
    private ProdukDao produkDao;

    public List<ProdukDto> listAllProduk() {
        return produkDao.findAll();
    }

    public ProdukDto getProdukById(Integer id) {
        return produkDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk", "id", id));
    }

    public int createProduk(ProdukDto produkDto) {
        return produkDao.save(produkDto);
    }

    public int updateProduk(ProdukDto produkDto) {
        return produkDao.update(produkDto);
    }

    public int deleteProduk(Integer id) {
        return produkDao.delete(id);
    }
}