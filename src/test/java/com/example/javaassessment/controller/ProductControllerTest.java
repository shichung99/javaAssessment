package com.example.javaassessment.controller;

import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.request.ProductRequest;
import com.example.javaassessment.response.MessageResponse;
import com.example.javaassessment.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        // Arrange
        List<ProductEntity> products = List.of(
                new ProductEntity(),
                new ProductEntity()
        );

        when(productService.getAllProducts())
                .thenReturn(products);

        // Act
        List<ProductEntity> result =
                productController.getAllProducts();

        // Assert
        assertEquals(products, result);
        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() {
        // Arrange
        when(productService.getAllProducts())
                .thenReturn(List.of());

        // Act
        List<ProductEntity> result =
                productController.getAllProducts();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void addProducts_shouldReturnCreatedProductWithLocation() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setProductName("Filip AC300");
        request.setProductType("Electronic");
        request.setPrice(new BigDecimal("19.20"));
        request.setStockQuantity(20);


        ProductEntity entity = new ProductEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        entity.setId(id);
        entity.setProductName("Filip AC300");
        entity.setProductType("Electronic");
        entity.setPrice(new BigDecimal("19.20"));
        entity.setStockQuantity(20);

        when(productService.addProduct(request))
                .thenReturn(entity);

        // Act
        ResponseEntity<?> response = productController.addProduct(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(entity, response.getBody());
        assertEquals(URI.create("/product/" + id), response.getHeaders().getLocation());
    }

    @Test
    void updateCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();
        request.setProductName("Filip AC300");
        request.setProductType("Electronic");
        request.setPrice(new BigDecimal("19.20"));
        request.setStockQuantity(20);

        MessageResponse expected =
                new MessageResponse("Successfully updated 123e4567-e89b-12d3-a456-426614174000, Filip AC300");

        when(productService.updateProduct(id, request))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = productController.updateProduct(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getMessage(), ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void patchCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();
        request.setProductType("Electronic");
        request.setPrice(new BigDecimal("19.20"));
        request.setStockQuantity(20);

        MessageResponse expected =
                new MessageResponse("Successfully patched 123e4567-e89b-12d3-a456-426614174000, Filip AC300");

        when(productService.patchProduct(id, request))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = productController.patchProduct(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getMessage(), ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void deleteProduct_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        MessageResponse expected =
                new MessageResponse("Successfully deleted 123e4567-e89b-12d3-a456-426614174000, Filip AC300");

        when(productService.deleteProduct(id))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = productController.deleteProduct(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected.getMessage(), ((MessageResponse)response.getBody()).getMessage());
    }
}
