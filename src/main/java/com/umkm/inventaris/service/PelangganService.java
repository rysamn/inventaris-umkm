package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.PelangganDao;
import com.umkm.inventaris.dto.PelangganDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PelangganService {

    @Autowired
    private PelangganDao pelangganDao;

    public List<PelangganDto> listAllPelanggan() {
        return pelangganDao.findAll();
    }

    public PelangganDto getPelangganById(Integer id) {
        return pelangganDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pelanggan", "id", id));
    }

    public int createPelanggan(PelangganDto pelangganDto) {
        return pelangganDao.save(pelangganDto);
    }

    public int updatePelanggan(PelangganDto pelangganDto) {
        return pelangganDao.update(pelangganDto);
    }

    public int deletePelanggan(Integer id) {
        return pelangganDao.delete(id);
    }
}