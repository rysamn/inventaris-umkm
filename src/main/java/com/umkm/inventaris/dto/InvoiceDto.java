package com.umkm.inventaris.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDto {
    private String invoiceNumber;
    private LocalDateTime tanggal;
    private String namaPelanggan;
    private String kasir;
    private List<InvoiceItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal pajak;
    private BigDecimal total;
}