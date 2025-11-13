package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.PelangganDao;
import com.umkm.inventaris.dao.PemasokDao;
import com.umkm.inventaris.dao.ProdukDao;
import com.umkm.inventaris.dto.DashboardDto;
import com.umkm.inventaris.dto.ProdukDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ProdukDao produkDao;

    @Autowired
    private PemasokDao pemasokDao;

    @Autowired
    private PelangganDao pelangganDao;

    @Autowired
    private LogInventarisService logInventarisService;

    public DashboardDto getDashboardData() {
        DashboardDto dashboard = new DashboardDto();
        
        // Total produk
        List<ProdukDto> allProduk = produkDao.findAll();
        dashboard.setTotalProduk(allProduk.size());
        
        // Total pemasok
        dashboard.setTotalPemasok(pemasokDao.findAll().size());
        
        // Total pelanggan
        dashboard.setTotalPelanggan(pelangganDao.findAll().size());
        
        // Produk dengan stok menipis (misalnya stok <= 10)
        List<ProdukDto> stokMenipis = allProduk.stream()
                .filter(p -> p.getStok() != null && p.getStok() <= 10)
                .collect(Collectors.toList());
        
        dashboard.setProdukStokMenipis(stokMenipis.size());
        dashboard.setDaftarStokMenipis(stokMenipis);
        
        // Aktivitas terbaru (10 log terakhir)
        dashboard.setAktivitasTerbaru(logInventarisService.listLatestLog(10));
        
        return dashboard;
    }
}