package com.umkm.inventaris.service;

import com.umkm.inventaris.dao.LogInventarisDao;
import com.umkm.inventaris.dto.LogInventarisDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogInventarisService {

    @Autowired
    private LogInventarisDao logInventarisDao;

    public List<LogInventarisDto> listAllLog() {
        return logInventarisDao.findAll();
    }

    public List<LogInventarisDto> listLatestLog(int limit) {
        return logInventarisDao.findLatest(limit);
    }

    public void createLog(Integer idProduk, String tipe, Integer jumlah, String keterangan) {
        LogInventarisDto log = new LogInventarisDto();
        log.setIdProduk(idProduk);
        log.setTipe(tipe);
        log.setJumlah(jumlah);
        log.setKeterangan(keterangan);
        logInventarisDao.save(log);
    }
}