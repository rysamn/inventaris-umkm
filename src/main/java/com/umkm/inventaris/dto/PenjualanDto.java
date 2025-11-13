package com.umkm.inventaris.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PenjualanDto {
    private Integer id;
    private Integer idPelanggan;
    private String namaPelanggan;
    private Integer idPengguna;
    private String namaPengguna;
    private LocalDateTime tanggal;
    private BigDecimal total;
    private List<DetailPenjualanDto> detailPenjualan;
}