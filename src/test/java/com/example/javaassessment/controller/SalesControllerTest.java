package com.example.javaassessment.controller;

import com.example.javaassessment.entity.SalesEntity;
import com.example.javaassessment.request.SalesRequest;
import com.example.javaassessment.response.MessageResponse;
import com.example.javaassessment.service.SalesService;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesControllerTest {
    @Mock
    private SalesService salesService;

    @InjectMocks
    private SalesController salesController;

    @Test
    void getAllSales_shouldReturnAllSales() {
        // Arrange
        List<SalesEntity> sales = List.of(
                new SalesEntity(),
                new SalesEntity()
        );

        when(salesService.getAllSales())
                .thenReturn(sales);

        // Act
        List<SalesEntity> result =
                salesController.getAllSales();

        // Assert
        assertEquals(sales, result);
        verify(salesService).getAllSales();
    }

    @Test
    void getAllCustomers_shouldReturnEmptyList() {
        // Arrange
        when(salesService.getAllSales())
                .thenReturn(List.of());

        // Act
        List<SalesEntity> result =
                salesController.getAllSales();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void addCustomers_shouldReturnCreatedCustomerWithLocation() {
        // Arrange
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174022");
        SalesRequest request = new SalesRequest();
        request.setProductId(productId);
        request.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        request.setSalesPrice(new BigDecimal("1900.40"));
        request.setSalesQuantity(100);

        SalesEntity savedSales = new SalesEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        savedSales.setId(id);
        savedSales.setProductId(productId);
        savedSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        savedSales.setSalesPrice(new BigDecimal("1900.40"));
        savedSales.setSalesQuantity(100);

        when(salesService.addSales(request))
                .thenReturn(savedSales);

        // Act
        ResponseEntity<?> response = salesController.addSales(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedSales, response.getBody());
        assertEquals(URI.create("/sales/" + id), response.getHeaders().getLocation());
    }

    @Test
    void updateCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174022");
        SalesRequest request = new SalesRequest();
        request.setProductId(productId);
        request.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        request.setSalesPrice(new BigDecimal("1900.40"));
        request.setSalesQuantity(100);

        MessageResponse expected =
                new MessageResponse("Successfully updated 123e4567-e89b-12d3-a456-426614174000");

        when(salesService.updateSales(id, request))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = salesController.updateSales(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getMessage(), ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void patchCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174022");
        SalesRequest request = new SalesRequest();
        request.setProductId(productId);
        request.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        request.setSalesPrice(new BigDecimal("1900.40"));
        request.setSalesQuantity(100);

        MessageResponse expected =
                new MessageResponse("Successfully patched 123e4567-e89b-12d3-a456-426614174000");

        when(salesService.patchSales(id, request))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = salesController.patchSales(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getMessage(), ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void deleteCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        MessageResponse expected =
                new MessageResponse("Successfully deleted 123e4567-e89b-12d3-a456-426614174000");

        when(salesService.deleteSales(id))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = salesController.deleteSales(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getMessage(), ((MessageResponse)response.getBody()).getMessage());
    }


    @Test
    void generateSalesReport_shouldReturnPdf() throws JRException {
        // Arrange
        LocalDate salesDate = LocalDate.of(2026, 7, 21);
        byte[] pdf = { 1, 2, 3 };

        when(salesService.generateSalesReport(salesDate))
                .thenReturn(pdf);

        // Act
        ResponseEntity<?> response =
                salesController.generateSalesReport(salesDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(pdf, (byte[]) response.getBody());

        assertEquals(
                MediaType.APPLICATION_PDF,
                response.getHeaders().getContentType()
        );

        assertEquals(
                "attachment; filename=sales_report.pdf",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
        );

        verify(salesService).generateSalesReport(salesDate);
    }

    @Test
    void generateSalesReport_shouldPropagateJRException() throws JRException {
        LocalDate salesDate = LocalDate.of(2026, 7, 21);

        when(salesService.generateSalesReport(salesDate))
                .thenThrow(new JRException("Report generation failed"));

        assertThrows(
                JRException.class,
                () -> salesController.generateSalesReport(salesDate)
        );

        verify(salesService).generateSalesReport(salesDate);
    }

}
