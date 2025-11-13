package com.umkm.inventaris.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetailPembelianDto {
    private Integer id;
    private Integer idPembelian;
    private Integer idProduk;
    private String namaProduk;
    private Integer jumlah;
    private BigDecimal harga;
    private BigDecimal subtotal;
}