package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.PenggunaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PenggunaDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<PenggunaDto> rowMapper = (rs, rowNum) -> {
        PenggunaDto dto = new PenggunaDto();
        dto.setId(rs.getInt("id"));
        dto.setNamaPengguna(rs.getString("nama_pengguna"));
        dto.setKataSandi(rs.getString("kata_sandi"));
        dto.setPeran(rs.getString("peran"));
        return dto;
    };

    public List<PenggunaDto> findAll() {
        String sql = "SELECT id, nama_pengguna, kata_sandi, peran FROM pengguna ORDER BY id";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<PenggunaDto> findById(Integer id) {
        String sql = "SELECT id, nama_pengguna, kata_sandi, peran FROM pengguna WHERE id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<PenggunaDto> findByNamaPengguna(String namaPengguna) {
        String sql = "SELECT id, nama_pengguna, kata_sandi, peran FROM pengguna WHERE nama_pengguna = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, namaPengguna));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(PenggunaDto p) {
        String sql = "INSERT INTO pengguna (nama_pengguna, kata_sandi, peran) VALUES (?, ?, ?)";
        return jdbc.update(sql, p.getNamaPengguna(), p.getKataSandi(), p.getPeran());
    }

    public int update(PenggunaDto p) {
        String sql = "UPDATE pengguna SET nama_pengguna = ?, kata_sandi = ?, peran = ? WHERE id = ?";
        return jdbc.update(sql, p.getNamaPengguna(), p.getKataSandi(), p.getPeran(), p.getId());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM pengguna WHERE id = ?";
        return jdbc.update(sql, id);
    }
}