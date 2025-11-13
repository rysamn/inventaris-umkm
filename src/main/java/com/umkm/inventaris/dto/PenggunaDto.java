package com.umkm.inventaris.dto;

import lombok.Data;

@Data
public class PenggunaDto {
    private Integer id;
    private String namaPengguna;
    private String kataSandi;
    private String peran; // admin, kasir, pemilik
}