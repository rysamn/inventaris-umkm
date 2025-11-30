package com.umkm.inventaris.config;

import com.umkm.inventaris.service.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PenggunaService penggunaService;

    @Autowired
    public void configureJwtFilter(UserDetailsService userDetailsService) {
        jwtRequestFilter.setUserDetailsService(userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of("*"));
                    configuration.setAllowedMethods(
                            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    return configuration;
                }))
                .authorizeHttpRequests(auth -> auth
                        // Endpoint yang dapat diakses publik (tanpa login)
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        // Hanya method GET untuk produk yang boleh diakses publik
                        .requestMatchers(HttpMethod.GET, "/api/produk/**").permitAll()

                        // Endpoint untuk kasir dan admin
                        .requestMatchers(HttpMethod.POST, "/api/penjualan").hasAnyRole("KASIR", "ADMIN")
                        .requestMatchers("/api/pelanggan/**").hasAnyRole("KASIR", "ADMIN")
                        .requestMatchers("/api/invoice/**").hasAnyRole("KASIR", "ADMIN")

                        // Endpoint yang hanya bisa diakses oleh admin
                        .requestMatchers("/api/produk/**").hasRole("ADMIN") // POST, PUT, DELETE produk
                        .requestMatchers("/api/pemasok/**").hasRole("ADMIN")
                        .requestMatchers("/api/pembelian/**").hasRole("ADMIN")
                        .requestMatchers("/api/pengguna/**").hasRole("ADMIN")
                        .requestMatchers("/api/log/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // Semua request lain harus terotentikasi
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return penggunaService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}