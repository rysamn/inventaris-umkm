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
    private byte[] fotoProduk;  // Ubah dari String ke byte[]
    private String fotoPreview;  // Untuk preview base64 di frontend
}