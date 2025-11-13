package com.umkm.inventaris.controller;

import com.google.zxing.WriterException;
import com.umkm.inventaris.service.BarcodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/barcode")
@CrossOrigin("*")
public class BarcodeController {

    private static final Logger logger = LoggerFactory.getLogger(BarcodeController.class);

    @Autowired
    private BarcodeService barcodeService;

    @GetMapping("/generate/{productId}")
    public ResponseEntity<byte[]> generateBarcode(@PathVariable Integer productId) {
        try {
            String barcodeText = barcodeService.generateBarcodeText(productId);
            byte[] barcodeImage = barcodeService.generateBarcode(barcodeText);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(barcodeImage.length);
            headers.set("Content-Disposition", "inline; filename=barcode-" + productId + ".png");

            return new ResponseEntity<>(barcodeImage, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            logger.error("Error generating barcode for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/text/{productId}")
    public ResponseEntity<String> getBarcodeText(@PathVariable Integer productId) {
        try {
            String barcodeText = barcodeService.generateBarcodeText(productId);
            return ResponseEntity.ok(barcodeText);
        } catch (Exception e) {
            logger.error("Error generating barcode text for product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating barcode text");
        }
    }
}