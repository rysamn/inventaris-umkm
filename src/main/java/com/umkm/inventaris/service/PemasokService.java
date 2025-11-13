package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.PemasokDao;
import com.umkm.inventaris.dto.PemasokDto;
import com.umkm.inventaris.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PemasokService {

    @Autowired
    private PemasokDao pemasokDao;

    public List<PemasokDto> listAllPemasok() {
        return pemasokDao.findAll();
    }

    public PemasokDto getPemasokById(Integer id) {
        return pemasokDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pemasok", "id", id));
    }

    public int createPemasok(PemasokDto pemasokDto) {
        return pemasokDao.save(pemasokDto);
    }

    public int updatePemasok(PemasokDto pemasokDto) {
        return pemasokDao.update(pemasokDto);
    }

    public int deletePemasok(Integer id) {
        return pemasokDao.delete(id);
    }
}