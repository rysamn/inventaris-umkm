package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.ProdukDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ProdukDao {
    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<ProdukDto> rowMapper = (rs, rowNum) -> {
        ProdukDto dto = new ProdukDto();
        dto.setId(rs.getInt("id"));
        dto.setNamaProduk(rs.getString("nama_produk"));
        dto.setHarga(rs.getBigDecimal("harga"));
        dto.setStok(rs.getInt("stok"));
        dto.setSatuan(rs.getString("satuan"));
        dto.setNamaKategori(rs.getString("nama_kategori"));
        dto.setFotoProduk(rs.getBytes("foto_produk"));  // Ubah ke getBytes
        return dto;
    };

    public List<ProdukDto> findAll() {
        String sql = "SELECT id, nama_produk, harga, stok, satuan, nama_kategori, foto_produk FROM produk ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<ProdukDto> findById(Integer id) {
        String sql = "SELECT id, nama_produk, harga, stok, satuan, nama_kategori, foto_produk FROM produk WHERE id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(ProdukDto p) {
        String sql = "INSERT INTO produk (nama_produk, harga, stok, satuan, nama_kategori, foto_produk) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbc.update(sql, p.getNamaProduk(), p.getHarga(), p.getStok(), p.getSatuan(), p.getNamaKategori(), p.getFotoProduk());
    }

    public int update(ProdukDto p) {
        String sql = "UPDATE produk SET nama_produk = ?, harga = ?, stok = ?, satuan = ?, nama_kategori = ?, foto_produk = ? WHERE id = ?";
        try {
            return jdbc.update(sql, 
                p.getNamaProduk(), 
                p.getHarga(), 
                p.getStok(), 
                p.getSatuan(), 
                p.getNamaKategori(), 
                p.getFotoProduk(),  // byte array
                p.getId()
            );
        } catch (Exception e) {
            logger.error("Error updating produk: {}", e.getMessage(), e);
            throw e;
        }
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM produk WHERE id = ?";
        return jdbc.update(sql, id);
    }

    private static final Logger logger = LoggerFactory.getLogger(ProdukDao.class);
}