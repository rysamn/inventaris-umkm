package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.ProdukDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return dto;
    };

    public List<ProdukDto> findAll() {
        String sql = "SELECT id, nama_produk, harga, stok, satuan, nama_kategori FROM produk ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<ProdukDto> findById(Integer id) {
        String sql = "SELECT id, nama_produk, harga, stok, satuan, nama_kategori FROM produk WHERE id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(ProdukDto p) {
        String sql = "INSERT INTO produk (nama_produk, harga, stok, satuan, nama_kategori) VALUES (?, ?, ?, ?, ?)";
        return jdbc.update(sql, p.getNamaProduk(), p.getHarga(), p.getStok(), p.getSatuan(), p.getNamaKategori());
    }

    public int update(ProdukDto p) {
        String sql = "UPDATE produk SET nama_produk = ?, harga = ?, stok = ?, satuan = ?, nama_kategori = ? WHERE id = ?";
        return jdbc.update(sql, p.getNamaProduk(), p.getHarga(), p.getStok(), p.getSatuan(), p.getNamaKategori(),
                p.getId());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM produk WHERE id = ?";
        return jdbc.update(sql, id);
    }
}