package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.PenggunaDao;
import com.umkm.inventaris.dto.LoginRequestDto;
import com.umkm.inventaris.dto.LoginResponseDto;
import com.umkm.inventaris.dto.PenggunaDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import com.umkm.inventaris.util.JwtUtil;
import com.umkm.inventaris.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenggunaService {

    @Autowired
    private PenggunaDao penggunaDao;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto request) {
        PenggunaDto pengguna = penggunaDao.findByNamaPengguna(request.getNamaPengguna())
                .orElseThrow(() -> new ResourceNotFoundException("Username atau password salah"));

        if (!passwordUtil.verifyPassword(request.getKataSandi(), pengguna.getKataSandi())) {
            throw new ResourceNotFoundException("Username atau password salah");
        }

        String token = jwtUtil.generateToken(pengguna.getNamaPengguna(), pengguna.getPeran(), pengguna.getId());

        return new LoginResponseDto(token, pengguna.getNamaPengguna(), pengguna.getPeran(), pengguna.getId());
    }

    public List<PenggunaDto> listAllPengguna() {
        List<PenggunaDto> users = penggunaDao.findAll();
        // Jangan return password ke client
        users.forEach(u -> u.setKataSandi(null));
        return users;
    }

    public PenggunaDto getPenggunaById(Integer id) {
        PenggunaDto pengguna = penggunaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna", "id", id));
        pengguna.setKataSandi(null); // Jangan return password
        return pengguna;
    }

    public PenggunaDto getPenggunaByUsername(String username) {
        PenggunaDto pengguna = penggunaDao.findByNamaPengguna(username)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna", "username", username));
        pengguna.setKataSandi(null); // Jangan return password
        return pengguna;
    }

    public int createPengguna(PenggunaDto penggunaDto) {
        // Hash password sebelum disimpan
        String hashedPassword = passwordUtil.hashPassword(penggunaDto.getKataSandi());
        penggunaDto.setKataSandi(hashedPassword);
        return penggunaDao.save(penggunaDto);
    }

    public int updatePengguna(PenggunaDto penggunaDto) {
        // Jika password diubah, hash ulang
        if (penggunaDto.getKataSandi() != null && !penggunaDto.getKataSandi().isEmpty()) {
            String hashedPassword = passwordUtil.hashPassword(penggunaDto.getKataSandi());
            penggunaDto.setKataSandi(hashedPassword);
        } else {
            // Jika password kosong, ambil password lama
            PenggunaDto existing = penggunaDao.findById(penggunaDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pengguna", "id", penggunaDto.getId()));
            penggunaDto.setKataSandi(existing.getKataSandi());
        }
        return penggunaDao.update(penggunaDto);
    }

    public int deletePengguna(Integer id) {
        return penggunaDao.delete(id);
    }
}