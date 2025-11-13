package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.PemasokDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PemasokDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<PemasokDto> rowMapper = (rs, rowNum) -> {
        PemasokDto dto = new PemasokDto();
        dto.setId(rs.getInt("id"));
        dto.setNama(rs.getString("nama"));
        dto.setTelepon(rs.getString("telepon"));
        dto.setAlamat(rs.getString("alamat"));
        return dto;
    };

    public List<PemasokDto> findAll() {
        String sql = "SELECT id, nama, telepon, alamat FROM pemasok ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<PemasokDto> findById(Integer id) {
        String sql = "SELECT id, nama, telepon, alamat FROM pemasok WHERE id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(PemasokDto p) {
        String sql = "INSERT INTO pemasok (nama, telepon, alamat) VALUES (?, ?, ?)";
        return jdbc.update(sql, p.getNama(), p.getTelepon(), p.getAlamat());
    }

    public int update(PemasokDto p) {
        String sql = "UPDATE pemasok SET nama = ?, telepon = ?, alamat = ? WHERE id = ?";
        return jdbc.update(sql, p.getNama(), p.getTelepon(), p.getAlamat(), p.getId());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM pemasok WHERE id = ?";
        return jdbc.update(sql, id);
    }
}