package com.umkm.inventaris.service;

import com.umkm.inventaris.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private PenjualanService penjualanService;

    public InvoiceDto generateInvoice(Integer idPenjualan) {
        PenjualanDto penjualan = penjualanService.getPenjualanById(idPenjualan);
        
        InvoiceDto invoice = new InvoiceDto();
        invoice.setInvoiceNumber("INV-" + String.format("%06d", penjualan.getId()));
        invoice.setTanggal(penjualan.getTanggal());
        invoice.setNamaPelanggan(penjualan.getNamaPelanggan() != null ? penjualan.getNamaPelanggan() : "Umum");
        invoice.setKasir(penjualan.getNamaPengguna());
        
        List<InvoiceItemDto> items = new ArrayList<>();
        for (DetailPenjualanDto detail : penjualan.getDetailPenjualan()) {
            InvoiceItemDto item = new InvoiceItemDto();
            item.setNamaProduk(detail.getNamaProduk());
            item.setJumlah(detail.getJumlah());
            item.setHarga(detail.getHarga());
            item.setSubtotal(detail.getHarga().multiply(BigDecimal.valueOf(detail.getJumlah())));
            items.add(item);
        }
        
        invoice.setItems(items);
        invoice.setSubtotal(penjualan.getTotal());
        invoice.setPajak(BigDecimal.ZERO); // Bisa diatur sesuai kebutuhan
        invoice.setTotal(penjualan.getTotal());
        
        return invoice;
    }

    public String generateInvoiceHtml(InvoiceDto invoice) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; max-width: 400px; margin: 0 auto; padding: 20px; }");
        html.append("h2 { text-align: center; margin-bottom: 5px; }");
        html.append(".header { text-align: center; border-bottom: 2px dashed #000; padding-bottom: 10px; margin-bottom: 10px; }");
        html.append(".info { margin-bottom: 10px; font-size: 12px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 10px; }");
        html.append("th, td { text-align: left; padding: 5px; font-size: 12px; }");
        html.append(".right { text-align: right; }");
        html.append(".total { border-top: 2px solid #000; font-weight: bold; font-size: 14px; }");
        html.append(".footer { text-align: center; margin-top: 20px; font-size: 11px; border-top: 2px dashed #000; padding-top: 10px; }");
        html.append("</style>");
        html.append("</head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h2>SISTEM INVENTARIS UMKM</h2>");
        html.append("<p style='margin:0; font-size:11px;'>Struk Pembelian</p>");
        html.append("</div>");
        
        // Info
        html.append("<div class='info'>");
        html.append("<p style='margin:2px 0;'>No. Invoice: <strong>").append(invoice.getInvoiceNumber()).append("</strong></p>");
        html.append("<p style='margin:2px 0;'>Tanggal: ").append(invoice.getTanggal()).append("</p>");
        html.append("<p style='margin:2px 0;'>Pelanggan: ").append(invoice.getNamaPelanggan()).append("</p>");
        html.append("<p style='margin:2px 0;'>Kasir: ").append(invoice.getKasir()).append("</p>");
        html.append("</div>");
        
        // Items
        html.append("<table>");
        html.append("<thead><tr><th>Item</th><th class='right'>Qty</th><th class='right'>Harga</th><th class='right'>Total</th></tr></thead>");
        html.append("<tbody>");
        
        for (InvoiceItemDto item : invoice.getItems()) {
            html.append("<tr>");
            html.append("<td>").append(item.getNamaProduk()).append("</td>");
            html.append("<td class='right'>").append(item.getJumlah()).append("</td>");
            html.append("<td class='right'>").append(String.format("Rp %,.0f", item.getHarga())).append("</td>");
            html.append("<td class='right'>").append(String.format("Rp %,.0f", item.getSubtotal())).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</tbody></table>");
        
        // Total
        html.append("<table class='total'>");
        html.append("<tr><td>Subtotal:</td><td class='right'>").append(String.format("Rp %,.0f", invoice.getSubtotal())).append("</td></tr>");
        if (invoice.getPajak().compareTo(BigDecimal.ZERO) > 0) {
            html.append("<tr><td>Pajak:</td><td class='right'>").append(String.format("Rp %,.0f", invoice.getPajak())).append("</td></tr>");
        }
        html.append("<tr><td><strong>TOTAL:</strong></td><td class='right'><strong>").append(String.format("Rp %,.0f", invoice.getTotal())).append("</strong></td></tr>");
        html.append("</table>");
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p style='margin:5px 0;'>Terima Kasih Atas Kunjungan Anda</p>");
        html.append("<p style='margin:5px 0;'>*** Barang yang sudah dibeli tidak dapat dikembalikan ***</p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
}