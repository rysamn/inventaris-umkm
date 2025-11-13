package com.umkm.inventaris.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceItemDto {
    private String namaProduk;
    private Integer jumlah;
    private BigDecimal harga;
    private BigDecimal subtotal;
}