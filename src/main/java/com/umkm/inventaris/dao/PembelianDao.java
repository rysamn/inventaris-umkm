package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.DetailPembelianDto;
import com.umkm.inventaris.dto.PembelianDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PembelianDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<PembelianDto> rowMapper = (rs, rowNum) -> {
        PembelianDto dto = new PembelianDto();
        dto.setId(rs.getInt("id"));
        dto.setIdPemasok(rs.getInt("id_pemasok"));
        dto.setNamaPemasok(rs.getString("nama_pemasok"));
        dto.setIdPengguna(rs.getInt("id_pengguna"));
        dto.setNamaPengguna(rs.getString("nama_pengguna"));
        Timestamp ts = rs.getTimestamp("tanggal");
        dto.setTanggal(ts != null ? ts.toLocalDateTime() : null);
        dto.setTotal(rs.getBigDecimal("total"));
        return dto;
    };

    private final RowMapper<DetailPembelianDto> detailRowMapper = (rs, rowNum) -> {
        DetailPembelianDto dto = new DetailPembelianDto();
        dto.setId(rs.getInt("id"));
        dto.setIdPembelian(rs.getInt("id_pembelian"));
        dto.setIdProduk(rs.getInt("id_produk"));
        dto.setNamaProduk(rs.getString("nama_produk"));
        dto.setJumlah(rs.getInt("jumlah"));
        dto.setHarga(rs.getBigDecimal("harga"));
        return dto;
    };

    public List<PembelianDto> findAll() {
        String sql = "SELECT p.id, p.id_pemasok, pm.nama as nama_pemasok, p.id_pengguna, " +
                     "pg.nama_pengguna, p.tanggal, p.total " +
                     "FROM pembelian p " +
                     "LEFT JOIN pemasok pm ON p.id_pemasok = pm.id " +
                     "LEFT JOIN pengguna pg ON p.id_pengguna = pg.id " +
                     "ORDER BY p.tanggal DESC";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<PembelianDto> findById(Integer id) {
        String sql = "SELECT p.id, p.id_pemasok, pm.nama as nama_pemasok, p.id_pengguna, " +
                     "pg.nama_pengguna, p.tanggal, p.total " +
                     "FROM pembelian p " +
                     "LEFT JOIN pemasok pm ON p.id_pemasok = pm.id " +
                     "LEFT JOIN pengguna pg ON p.id_pengguna = pg.id " +
                     "WHERE p.id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<DetailPembelianDto> findDetailByPembelianId(Integer idPembelian) {
        String sql = "SELECT d.id, d.id_pembelian, d.id_produk, p.nama_produk, d.jumlah, d.harga " +
                     "FROM detail_pembelian d " +
                     "LEFT JOIN produk p ON d.id_produk = p.id " +
                     "WHERE d.id_pembelian = ?";
        return jdbc.query(sql, detailRowMapper, idPembelian);
    }

    public int save(PembelianDto pembelian, KeyHolder keyHolder) {
        String sql = "INSERT INTO pembelian (id_pemasok, id_pengguna, total) VALUES (?, ?, ?)";
        return jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pembelian.getIdPemasok());
            ps.setInt(2, pembelian.getIdPengguna());
            ps.setBigDecimal(3, pembelian.getTotal());
            return ps;
        }, keyHolder);
    }

    public int saveDetail(DetailPembelianDto detail) {
        String sql = "INSERT INTO detail_pembelian (id_pembelian, id_produk, jumlah, harga) VALUES (?, ?, ?, ?)";
        return jdbc.update(sql, detail.getIdPembelian(), detail.getIdProduk(), detail.getJumlah(), detail.getHarga());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM pembelian WHERE id = ?";
        return jdbc.update(sql, id);
    }
}