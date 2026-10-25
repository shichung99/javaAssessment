package com.example.javaassessment.service;

import com.example.javaassessment.entity.SalesEntity;
import com.example.javaassessment.exception.ResourceNotFoundException;
import com.example.javaassessment.repository.SalesRepository;
import com.example.javaassessment.request.SalesRequest;
import com.example.javaassessment.request.SalesRequest;
import com.example.javaassessment.response.MessageResponse;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesServiceTest {
    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesService salesService;

    @Test
    void getAllSales_shouldReturnList(){
        // Arrange
        List<SalesEntity> sales = List.of(
                new SalesEntity(),
                new SalesEntity()
        );

        when(salesRepository.findAll())
                .thenReturn(sales);

        // Act
        List<SalesEntity> result =
                salesService.getAllSales();

        // Assert
        assertEquals(sales, result);
        verify(salesRepository).findAll();
    }

    @Test
    void getAllSales_shouldReturnEmptyList() {
        // Arrange
        when(salesRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<SalesEntity> result =
                salesService.getAllSales();

        // Assert
        assertTrue(result.isEmpty());
        verify(salesRepository).findAll();
    }

    @Test
    void addSales_shouldReturnCreatedCustomer() {
        // Arrange
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174022");
        SalesRequest request = new SalesRequest();
        request.setProductId(productId);
        request.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        request.setSalesPrice(new BigDecimal("1900.40"));
        request.setSalesQuantity(100);

        SalesEntity toBeSavedSales = new SalesEntity();
        toBeSavedSales.setProductId(productId);
        toBeSavedSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        toBeSavedSales.setSalesPrice(new BigDecimal("1900.40"));
        toBeSavedSales.setSalesQuantity(100);


        SalesEntity savedSales = new SalesEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        savedSales.setId(id);
        savedSales.setProductId(productId);
        savedSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        savedSales.setSalesPrice(new BigDecimal("1900.40"));
        savedSales.setSalesQuantity(100);

        when(salesRepository.save(any(SalesEntity.class)))
                .thenReturn(savedSales);

        // Act
        SalesEntity result = salesService.addSales(request);

        // Assert
        assertEquals(savedSales, result);
        verify(salesRepository).save(any(SalesEntity.class));

        ArgumentCaptor<SalesEntity> captor =
                ArgumentCaptor.forClass(SalesEntity.class);
        verify(salesRepository).save(captor.capture());
        SalesEntity captured = captor.getValue();

        assertEquals(productId, captured.getProductId());
        assertEquals(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")), captured.getSalesDate());
        assertEquals(new BigDecimal("1900.40"), captured.getSalesPrice());
        assertEquals(100, captured.getSalesQuantity());
    }

    @Test
    void updateCustomer_shouldReturnSuccessResponse(){
        //Finds the record using the correct UUID.
        //Updates all five fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174022");
        SalesRequest request = new SalesRequest();
        request.setProductId(productId);
        request.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        request.setSalesPrice(new BigDecimal("1900.40"));
        request.setSalesQuantity(100);

        SalesEntity existingSales = new SalesEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        existingSales.setId(id);
        existingSales.setProductId(productId);
        existingSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        existingSales.setSalesPrice(new BigDecimal("1900.40"));
        existingSales.setSalesQuantity(100);

        SalesEntity savedProduct = new SalesEntity();
        savedProduct.setId(id);
        savedProduct.setProductId(productId);
        savedProduct.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        savedProduct.setSalesPrice(new BigDecimal("1900.40"));
        savedProduct.setSalesQuantity(100);

        when(salesRepository.save(any(SalesEntity.class)))
                .thenReturn(savedProduct);

        when(salesRepository.findById(id))
                .thenReturn(Optional.of(existingSales));

        MessageResponse result = salesService.updateSales(id, request);

        // Assert
        assertEquals("Successfully updated 123e4567-e89b-12d3-a456-426614174000", result.getMessage());

        ArgumentCaptor<SalesEntity> captor =
                ArgumentCaptor.forClass(SalesEntity.class);
        verify(salesRepository).save(captor.capture());
        verify(salesRepository).findById(id);
        SalesEntity captured = captor.getValue();

        assertEquals(productId, captured.getProductId());
        assertEquals(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")), captured.getSalesDate());
        assertEquals(new BigDecimal("1900.40"), captured.getSalesPrice());
        assertEquals(100, captured.getSalesQuantity());
    }

    @Test
    void updateSales_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        // Arrange
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        SalesRequest request = new SalesRequest();
        request.setProductId(productId);
        request.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        request.setSalesPrice(new BigDecimal("1900.40"));
        request.setSalesQuantity(100);

        when(salesRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> salesService.updateSales(id, request)
        );
        verify(salesRepository).findById(id);
        verify(salesRepository, never()).save(any(SalesEntity.class));
    }

    @Test
    void patchCustomer_shouldUpdateProvidedFieldsOnly(){
        //Finds the customer using the correct UUID.
        //Updates only fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174222");
        SalesRequest request = new SalesRequest();
        request.setSalesQuantity(80);

        SalesEntity existingSales = new SalesEntity();
        existingSales.setId(id);
        existingSales.setProductId(productId);
        existingSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        existingSales.setSalesPrice(new BigDecimal("1900.40"));
        existingSales.setSalesQuantity(100);

        when(salesRepository.findById(id))
                .thenReturn(Optional.of(existingSales));

        MessageResponse result = salesService.patchSales(id, request);

        // Assert
        assertEquals("Successfully patched 123e4567-e89b-12d3-a456-426614174000", result.getMessage());

        ArgumentCaptor<SalesEntity> captor =
                ArgumentCaptor.forClass(SalesEntity.class);
        verify(salesRepository).findById(id);
        verify(salesRepository).save(captor.capture());
        SalesEntity captured = captor.getValue();

        assertEquals(productId, captured.getProductId());
        assertEquals(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")), captured.getSalesDate());
        assertEquals(new BigDecimal("1900.40"), captured.getSalesPrice());
        assertEquals(80, captured.getSalesQuantity());
    }

    @Test
    void patchCustomer_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        //Never performs save
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        SalesRequest request = new SalesRequest();
        request.setSalesQuantity(80);


        when(salesRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> salesService.patchSales(id, request)
        );
        verify(salesRepository).findById(id);
        verify(salesRepository, never()).save(any(SalesEntity.class));
    }

    @Test
    void patchCustomer_shouldUpdateNothingIfAllFieldsNull(){
        //Finds the customer using the correct UUID.
        //Updates only fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174222");
        SalesRequest request = new SalesRequest();

        SalesEntity existingSales = new SalesEntity();
        existingSales.setId(id);
        existingSales.setProductId(productId);
        existingSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        existingSales.setSalesPrice(new BigDecimal("1900.40"));
        existingSales.setSalesQuantity(100);

        when(salesRepository.findById(id))
                .thenReturn(Optional.of(existingSales));

        MessageResponse result = salesService.patchSales(id, request);

        // Assert
        assertEquals("Successfully patched 123e4567-e89b-12d3-a456-426614174000", result.getMessage());

        ArgumentCaptor<SalesEntity> captor =
                ArgumentCaptor.forClass(SalesEntity.class);
        verify(salesRepository).findById(id);
        verify(salesRepository).save(captor.capture());
        SalesEntity captured = captor.getValue();

        assertEquals(productId, captured.getProductId());
        assertEquals(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")), captured.getSalesDate());
        assertEquals(new BigDecimal("1900.40"), captured.getSalesPrice());
        assertEquals(100, captured.getSalesQuantity());
    }

    @Test
    void deleteCustomer_shouldDeleteCustomerAndReturnMessage() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174222");
        SalesEntity existingSales = new SalesEntity();
        existingSales.setId(id);
        existingSales.setProductId(productId);
        existingSales.setSalesDate(LocalDate.parse("21/07/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        existingSales.setSalesPrice(new BigDecimal("1900.40"));
        existingSales.setSalesQuantity(100);


        MessageResponse expected =
                new MessageResponse("Successfully deleted 123e4567-e89b-12d3-a456-426614174000");

        when(salesRepository.findById(id))
                .thenReturn(Optional.of(existingSales));

        // Act
        MessageResponse response = salesService.deleteSales(id);

        // Assert
        assertEquals(expected.getMessage(), response.getMessage());
        verify(salesRepository).findById(id);
        verify(salesRepository).delete(existingSales);
    }

    @Test
    void deleteCustomer_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        //Never performs delete.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(salesRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> salesService.deleteSales(id)
        );
        verify(salesRepository).findById(id);
        verify(salesRepository, never()).delete(any(SalesEntity.class));
    }

    @Test
    void generateSalesReport_shouldReturnPdfBytes() throws JRException {
        // Arrange
        LocalDate salesDate = LocalDate.of(2026, 7, 21);

        SalesEntity sale = new SalesEntity();
        sale.setSalesDate(salesDate);
        when(salesRepository.findBySalesDate(salesDate))
                .thenReturn(List.of(sale));


        // Act
        byte[] result = salesService.generateSalesReport(salesDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(salesRepository).findBySalesDate(salesDate);
    }


}
