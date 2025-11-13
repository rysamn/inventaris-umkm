package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.PelangganDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PelangganDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<PelangganDto> rowMapper = (rs, rowNum) -> {
        PelangganDto dto = new PelangganDto();
        dto.setId(rs.getInt("id"));
        dto.setNamaPelanggan(rs.getString("nama_pelanggan"));
        dto.setTelepon(rs.getString("telepon"));
        dto.setAlamat(rs.getString("alamat"));
        return dto;
    };

    public List<PelangganDto> findAll() {
        String sql = "SELECT id, nama_pelanggan, telepon, alamat FROM pelanggan ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<PelangganDto> findById(Integer id) {
        String sql = "SELECT id, nama_pelanggan, telepon, alamat FROM pelanggan WHERE id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(PelangganDto p) {
        String sql = "INSERT INTO pelanggan (nama_pelanggan, telepon, alamat) VALUES (?, ?, ?)";
        return jdbc.update(sql, p.getNamaPelanggan(), p.getTelepon(), p.getAlamat());
    }

    public int update(PelangganDto p) {
        String sql = "UPDATE pelanggan SET nama_pelanggan = ?, telepon = ?, alamat = ? WHERE id = ?";
        return jdbc.update(sql, p.getNamaPelanggan(), p.getTelepon(), p.getAlamat(), p.getId());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM pelanggan WHERE id = ?";
        return jdbc.update(sql, id);
    }
}