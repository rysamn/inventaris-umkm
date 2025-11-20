package com.umkm.inventaris.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdukDto {
    private Integer id;
    private String namaProduk;
    private BigDecimal harga;
    private Integer stok;
    private String satuan;
    private String namaKategori;
    private String fotoProduk;
}