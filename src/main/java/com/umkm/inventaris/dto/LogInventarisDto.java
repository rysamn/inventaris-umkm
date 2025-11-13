package com.umkm.inventaris.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogInventarisDto {
    private Integer id;
    private Integer idProduk;
    private String namaProduk;
    private String tipe; // masuk / keluar
    private Integer jumlah;
    private String keterangan;
    private LocalDateTime tanggal;
}