package com.umkm.inventaris.dao;

import com.umkm.inventaris.dto.DetailPenjualanDto;
import com.umkm.inventaris.dto.PenjualanDto;
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
public class PenjualanDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<PenjualanDto> rowMapper = (rs, rowNum) -> {
        PenjualanDto dto = new PenjualanDto();
        dto.setId(rs.getInt("id"));
        dto.setIdPelanggan(rs.getInt("id_pelanggan"));
        dto.setNamaPelanggan(rs.getString("nama_pelanggan"));
        dto.setIdPengguna(rs.getInt("id_pengguna"));
        dto.setNamaPengguna(rs.getString("nama_pengguna"));
        Timestamp ts = rs.getTimestamp("tanggal");
        dto.setTanggal(ts != null ? ts.toLocalDateTime() : null);
        dto.setTotal(rs.getBigDecimal("total"));
        return dto;
    };

    private final RowMapper<DetailPenjualanDto> detailRowMapper = (rs, rowNum) -> {
        DetailPenjualanDto dto = new DetailPenjualanDto();
        dto.setId(rs.getInt("id"));
        dto.setIdPenjualan(rs.getInt("id_penjualan"));
        dto.setIdProduk(rs.getInt("id_produk"));
        dto.setNamaProduk(rs.getString("nama_produk"));
        dto.setJumlah(rs.getInt("jumlah"));
        dto.setHarga(rs.getBigDecimal("harga"));
        return dto;
    };

    public List<PenjualanDto> findAll() {
        String sql = "SELECT p.id, p.id_pelanggan, pl.nama_pelanggan, p.id_pengguna, " +
                     "pg.nama_pengguna, p.tanggal, p.total " +
                     "FROM penjualan p " +
                     "LEFT JOIN pelanggan pl ON p.id_pelanggan = pl.id " +
                     "LEFT JOIN pengguna pg ON p.id_pengguna = pg.id " +
                     "ORDER BY p.tanggal DESC";
        return jdbc.query(sql, rowMapper);
    }

    public Optional<PenjualanDto> findById(Integer id) {
        String sql = "SELECT p.id, p.id_pelanggan, pl.nama_pelanggan, p.id_pengguna, " +
                     "pg.nama_pengguna, p.tanggal, p.total " +
                     "FROM penjualan p " +
                     "LEFT JOIN pelanggan pl ON p.id_pelanggan = pl.id " +
                     "LEFT JOIN pengguna pg ON p.id_pengguna = pg.id " +
                     "WHERE p.id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<DetailPenjualanDto> findDetailByPenjualanId(Integer idPenjualan) {
        String sql = "SELECT d.id, d.id_penjualan, d.id_produk, p.nama_produk, d.jumlah, d.harga " +
                     "FROM detail_penjualan d " +
                     "LEFT JOIN produk p ON d.id_produk = p.id " +
                     "WHERE d.id_penjualan = ?";
        return jdbc.query(sql, detailRowMapper, idPenjualan);
    }

    public int save(PenjualanDto penjualan, KeyHolder keyHolder) {
        String sql = "INSERT INTO penjualan (id_pelanggan, id_pengguna, total) VALUES (?, ?, ?)";
        return jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"}); // HANYA RETURN ID
            ps.setInt(1, penjualan.getIdPelanggan());
            ps.setInt(2, penjualan.getIdPengguna());
            ps.setBigDecimal(3, penjualan.getTotal());
            return ps;
        }, keyHolder);
    }

    public int saveDetail(DetailPenjualanDto detail) {
        String sql = "INSERT INTO detail_penjualan (id_penjualan, id_produk, jumlah, harga) VALUES (?, ?, ?, ?)";
        return jdbc.update(sql, detail.getIdPenjualan(), detail.getIdProduk(), detail.getJumlah(), detail.getHarga());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM penjualan WHERE id = ?";
        return jdbc.update(sql, id);
    }
}