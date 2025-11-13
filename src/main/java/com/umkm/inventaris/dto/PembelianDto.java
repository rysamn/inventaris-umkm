package com.umkm.inventaris.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PembelianDto {
    private Integer id;
    private Integer idPemasok;
    private String namaPemasok;
    private Integer idPengguna;
    private String namaPengguna;
    private LocalDateTime tanggal;
    private BigDecimal total;
    private List<DetailPembelianDto> detailPembelian;
}