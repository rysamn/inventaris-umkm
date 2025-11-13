package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.PembelianDao;
import com.umkm.inventaris.dao.ProdukDao;
import com.umkm.inventaris.dto.DetailPembelianDto;
import com.umkm.inventaris.dto.PembelianDto;
import com.umkm.inventaris.dto.ProdukDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PembelianService {

    @Autowired
    private PembelianDao pembelianDao;

    @Autowired
    private ProdukDao produkDao;

    @Autowired
    private LogInventarisService logInventarisService;

    public List<PembelianDto> listAllPembelian() {
        List<PembelianDto> pembelianList = pembelianDao.findAll();
        // Load detail untuk setiap pembelian
        pembelianList.forEach(p -> {
            List<DetailPembelianDto> details = pembelianDao.findDetailByPembelianId(p.getId());
            details.forEach(d -> {
                d.setSubtotal(d.getHarga().multiply(BigDecimal.valueOf(d.getJumlah())));
            });
            p.setDetailPembelian(details);
        });
        return pembelianList;
    }

    public PembelianDto getPembelianById(Integer id) {
        PembelianDto pembelian = pembelianDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pembelian", "id", id));
        
        List<DetailPembelianDto> details = pembelianDao.findDetailByPembelianId(id);
        details.forEach(d -> {
            d.setSubtotal(d.getHarga().multiply(BigDecimal.valueOf(d.getJumlah())));
        });
        pembelian.setDetailPembelian(details);
        
        return pembelian;
    }

    @Transactional
    public int createPembelian(PembelianDto pembelianDto) {
        // 1. Simpan header pembelian
        KeyHolder keyHolder = new GeneratedKeyHolder();
        pembelianDao.save(pembelianDto, keyHolder);
        
        Integer idPembelian = keyHolder.getKey().intValue();
        
        // 2. Simpan detail pembelian dan update stok produk
        for (DetailPembelianDto detail : pembelianDto.getDetailPembelian()) {
            detail.setIdPembelian(idPembelian);
            pembelianDao.saveDetail(detail);
            
            // 3. Update stok produk (tambah stok)
            ProdukDto produk = produkDao.findById(detail.getIdProduk())
                    .orElseThrow(() -> new ResourceNotFoundException("Produk", "id", detail.getIdProduk()));
            
            produk.setStok(produk.getStok() + detail.getJumlah());
            produkDao.update(produk);
            
            // 4. Catat log inventaris
            logInventarisService.createLog(
                detail.getIdProduk(), 
                "masuk", 
                detail.getJumlah(), 
                "Pembelian dari supplier ID: " + pembelianDto.getIdPemasok()
            );
        }
        
        return 1;
    }

    @Transactional
    public int deletePembelian(Integer id) {
        // Get detail pembelian dulu sebelum dihapus
        List<DetailPembelianDto> details = pembelianDao.findDetailByPembelianId(id);
        
        // Kembalikan stok produk
        for (DetailPembelianDto detail : details) {
            ProdukDto produk = produkDao.findById(detail.getIdProduk())
                    .orElseThrow(() -> new ResourceNotFoundException("Produk", "id", detail.getIdProduk()));
            
            produk.setStok(produk.getStok() - detail.getJumlah());
            produkDao.update(produk);
            
            // Log
            logInventarisService.createLog(
                detail.getIdProduk(), 
                "keluar", 
                detail.getJumlah(), 
                "Pembatalan pembelian ID: " + id
            );
        }
        
        // Hapus pembelian (detail akan terhapus otomatis karena CASCADE)
        return pembelianDao.delete(id);
    }
}