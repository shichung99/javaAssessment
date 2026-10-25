package com.example.javaassessment.controller;

import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.entity.SalesEntity;
import com.example.javaassessment.request.ProductRequest;
import com.example.javaassessment.request.SalesRequest;
import com.example.javaassessment.response.MessageResponse;
import com.example.javaassessment.service.ProductService;
import com.example.javaassessment.service.SalesService;
import net.sf.jasperreports.engine.JRException;
import org.aspectj.bridge.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales")
public class SalesController {
    private final SalesService salesService;
    private static final Logger logger = LoggerFactory.getLogger(SalesController.class);
    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public List<SalesEntity> getAllSales(){
        return salesService.getAllSales();
    }

    @PostMapping
    public ResponseEntity<SalesEntity> addSales(
            @RequestBody SalesRequest request
    ){
        logger.debug("[SalesController] addCustomer: {}", request);
        SalesEntity savedSale = salesService.addSales(request);

        return ResponseEntity
                .created(URI.create("/sales/" + savedSale.getId()))
                .body(savedSale);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateSales(
            @PathVariable UUID id,
            @RequestBody SalesRequest request
    ){
        logger.info("[SalesController] updateSales: ID={}", id);
        logger.debug("[SalesController] updateSales: {}", request);
        MessageResponse response = salesService.updateSales(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> patchSales(
            @PathVariable UUID id,
            @RequestBody SalesRequest request
    ){
        logger.info("[SalesController] patchSales: ID={}", id);
        logger.debug("[SalesController] patchSales: {}", request);
        MessageResponse response = salesService.patchSales(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteSales(
            @PathVariable UUID id
    ){
        logger.info("[SalesController] deleteSales: ID={}", id);
        MessageResponse response = salesService.deleteSales(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> generateSalesReport(
            @RequestParam LocalDate salesDate
    ) throws JRException {
        logger.info("[SalesController] Get Report from date: {}", salesDate);
        byte[] pdf = salesService.generateSalesReport(salesDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sales_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
