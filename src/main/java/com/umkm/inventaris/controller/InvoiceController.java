package com.umkm.inventaris.controller;

import com.umkm.inventaris.dto.InvoiceDto;
import com.umkm.inventaris.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin("*")
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/{idPenjualan}")
    public ResponseEntity<?> getInvoice(@PathVariable Integer idPenjualan) {
        try {
            InvoiceDto invoice = invoiceService.generateInvoice(idPenjualan);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            logger.error("Error generating invoice for penjualan {}: {}", idPenjualan, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating invoice: " + e.getMessage());
        }
    }

    @GetMapping(value = "/print/{idPenjualan}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> printInvoice(@PathVariable Integer idPenjualan) {
        try {
            InvoiceDto invoice = invoiceService.generateInvoice(idPenjualan);
            String html = invoiceService.generateInvoiceHtml(invoice);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        } catch (Exception e) {
            logger.error("Error printing invoice for penjualan {}: {}", idPenjualan, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("<html><body><h1>Error generating invoice</h1><p>" + e.getMessage() + "</p></body></html>");
        }
    }
}