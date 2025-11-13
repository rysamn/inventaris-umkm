package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.PenjualanDao;
import com.umkm.inventaris.dao.ProdukDao;
import com.umkm.inventaris.dto.DetailPenjualanDto;
import com.umkm.inventaris.dto.PenjualanDto;
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
public class PenjualanService {

    @Autowired
    private PenjualanDao penjualanDao;

    @Autowired
    private ProdukDao produkDao;

    @Autowired
    private LogInventarisService logInventarisService;

    public List<PenjualanDto> listAllPenjualan() {
        List<PenjualanDto> penjualanList = penjualanDao.findAll();
        // Load detail untuk setiap penjualan
        penjualanList.forEach(p -> {
            List<DetailPenjualanDto> details = penjualanDao.findDetailByPenjualanId(p.getId());
            details.forEach(d -> {
                d.setSubtotal(d.getHarga().multiply(BigDecimal.valueOf(d.getJumlah())));
            });
            p.setDetailPenjualan(details);
        });
        return penjualanList;
    }

    public PenjualanDto getPenjualanById(Integer id) {
        PenjualanDto penjualan = penjualanDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Penjualan", "id", id));
        
        List<DetailPenjualanDto> details = penjualanDao.findDetailByPenjualanId(id);
        details.forEach(d -> {
            d.setSubtotal(d.getHarga().multiply(BigDecimal.valueOf(d.getJumlah())));
        });
        penjualan.setDetailPenjualan(details);
        
        return penjualan;
    }

    @Transactional
    public int createPenjualan(PenjualanDto penjualanDto) {
        // 1. Simpan header penjualan
        KeyHolder keyHolder = new GeneratedKeyHolder();
        penjualanDao.save(penjualanDto, keyHolder);
        
        Integer idPenjualan = keyHolder.getKey().intValue();
        
        // 2. Simpan detail penjualan dan update stok produk
        for (DetailPenjualanDto detail : penjualanDto.getDetailPenjualan()) {
            detail.setIdPenjualan(idPenjualan);
            penjualanDao.saveDetail(detail);
            
            // 3. Update stok produk (kurangi stok)
            ProdukDto produk = produkDao.findById(detail.getIdProduk())
                    .orElseThrow(() -> new ResourceNotFoundException("Produk", "id", detail.getIdProduk()));
            
            // Validasi stok cukup
            if (produk.getStok() < detail.getJumlah()) {
                throw new RuntimeException("Stok produk " + produk.getNamaProduk() + " tidak mencukupi. Stok tersedia: " + produk.getStok());
            }
            
            produk.setStok(produk.getStok() - detail.getJumlah());
            produkDao.update(produk);
            
            // 4. Catat log inventaris
            logInventarisService.createLog(
                detail.getIdProduk(), 
                "keluar", 
                detail.getJumlah(), 
                "Penjualan ke pelanggan ID: " + penjualanDto.getIdPelanggan()
            );
        }
        
        return 1;
    }

    @Transactional
    public int deletePenjualan(Integer id) {
        // Get detail penjualan dulu sebelum dihapus
        List<DetailPenjualanDto> details = penjualanDao.findDetailByPenjualanId(id);
        
        // Kembalikan stok produk
        for (DetailPenjualanDto detail : details) {
            ProdukDto produk = produkDao.findById(detail.getIdProduk())
                    .orElseThrow(() -> new ResourceNotFoundException("Produk", "id", detail.getIdProduk()));
            
            produk.setStok(produk.getStok() + detail.getJumlah());
            produkDao.update(produk);
            
            // Log
            logInventarisService.createLog(
                detail.getIdProduk(), 
                "masuk", 
                detail.getJumlah(), 
                "Pembatalan penjualan ID: " + id
            );
        }
        
        // Hapus penjualan (detail akan terhapus otomatis karena CASCADE)
        return penjualanDao.delete(id);
    }
}