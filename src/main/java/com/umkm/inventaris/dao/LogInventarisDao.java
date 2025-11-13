package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.LogInventarisDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class LogInventarisDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<LogInventarisDto> rowMapper = (rs, rowNum) -> {
        LogInventarisDto dto = new LogInventarisDto();
        dto.setId(rs.getInt("id"));
        dto.setIdProduk(rs.getInt("id_produk"));
        dto.setNamaProduk(rs.getString("nama_produk"));
        dto.setTipe(rs.getString("tipe"));
        dto.setJumlah(rs.getInt("jumlah"));
        dto.setKeterangan(rs.getString("keterangan"));
        Timestamp ts = rs.getTimestamp("tanggal");
        dto.setTanggal(ts != null ? ts.toLocalDateTime() : null);
        return dto;
    };

    public List<LogInventarisDto> findAll() {
        String sql = "SELECT l.id, l.id_produk, p.nama_produk, l.tipe, l.jumlah, l.keterangan, l.tanggal " +
                     "FROM log_inventaris l " +
                     "LEFT JOIN produk p ON l.id_produk = p.id " +
                     "ORDER BY l.tanggal DESC";
        return jdbc.query(sql, rowMapper);
    }

    public List<LogInventarisDto> findLatest(int limit) {
        String sql = "SELECT l.id, l.id_produk, p.nama_produk, l.tipe, l.jumlah, l.keterangan, l.tanggal " +
                     "FROM log_inventaris l " +
                     "LEFT JOIN produk p ON l.id_produk = p.id " +
                     "ORDER BY l.tanggal DESC LIMIT ?";
        return jdbc.query(sql, rowMapper, limit);
    }

    public Optional<LogInventarisDto> findById(Integer id) {
        String sql = "SELECT l.id, l.id_produk, p.nama_produk, l.tipe, l.jumlah, l.keterangan, l.tanggal " +
                     "FROM log_inventaris l " +
                     "LEFT JOIN produk p ON l.id_produk = p.id " +
                     "WHERE l.id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int save(LogInventarisDto log) {
        String sql = "INSERT INTO log_inventaris (id_produk, tipe, jumlah, keterangan) VALUES (?, ?, ?, ?)";
        return jdbc.update(sql, log.getIdProduk(), log.getTipe(), log.getJumlah(), log.getKeterangan());
    }
}