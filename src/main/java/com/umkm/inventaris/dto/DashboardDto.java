package com.umkm.inventaris.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardDto {
    private Integer totalProduk;
    private Integer totalPemasok;
    private Integer totalPelanggan;
    private Integer produkStokMenipis;
    private List<ProdukDto> daftarStokMenipis;
    private List<LogInventarisDto> aktivitasTerbaru;
}