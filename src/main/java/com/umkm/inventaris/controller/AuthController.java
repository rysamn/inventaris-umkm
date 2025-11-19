package com.umkm.inventaris.controller;

import com.umkm.inventaris.util.JwtUtil;
import com.umkm.inventaris.dto.LoginRequestDto;
import com.umkm.inventaris.dto.PenggunaDto;
import com.umkm.inventaris.dto.LoginResponseDto;
import com.umkm.inventaris.service.PenggunaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PenggunaService penggunaService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // Otentikasi menggunakan Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getNamaPengguna(),
                            loginRequest.getKataSandi()));
        } catch (BadCredentialsException e) {
            logger.warn("Login gagal untuk pengguna: {}", loginRequest.getNamaPengguna());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Username atau password salah");
        }

        // Jika otentikasi berhasil, buat token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getNamaPengguna());
        final PenggunaDto pengguna = penggunaService.getPenggunaByUsername(loginRequest.getNamaPengguna());
        final String token = jwtUtil.generateToken(userDetails.getUsername(), pengguna.getPeran(), pengguna.getId());

        return ResponseEntity
                .ok(new LoginResponseDto(token, pengguna.getNamaPengguna(), pengguna.getPeran(), pengguna.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PenggunaDto penggunaDto) {
        try {
            penggunaService.createPengguna(penggunaDto);
            return new ResponseEntity<>("Pengguna berhasil dibuat.", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error saat membuat pengguna: {}", e.getMessage(), e);
            return new ResponseEntity<>("Gagal membuat pengguna. Nama pengguna mungkin sudah ada.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}