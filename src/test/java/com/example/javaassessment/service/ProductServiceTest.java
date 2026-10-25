package com.example.javaassessment.service;


import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.exception.BusinessException;
import com.example.javaassessment.exception.ResourceNotFoundException;
import com.example.javaassessment.repository.ProductRepository;
import com.example.javaassessment.request.ProductRequest;
import com.example.javaassessment.response.MessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_shouldReturnList(){
        // Arrange
        List<ProductEntity> products = List.of(
                new ProductEntity(),
                new ProductEntity()
        );

        when(productRepository.findAll())
                .thenReturn(products);

        // Act
        List<ProductEntity> result =
                productService.getAllProducts();

        // Assert
        assertEquals(products, result);
        verify(productRepository).findAll();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<ProductEntity> result =
                productService.getAllProducts();

        // Assert
        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    void addProduct_shouldReturnCreatedCustomer() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setProductName("Filip AC300");
        request.setProductType("Electronic");
        request.setPrice(new BigDecimal("19.20"));
        request.setStockQuantity(20);

        ProductEntity toBeSavedProduct = new ProductEntity();
        toBeSavedProduct.setProductName("Filip AC300");
        toBeSavedProduct.setProductType("Electronic");
        toBeSavedProduct.setPrice(new BigDecimal("19.20"));
        toBeSavedProduct.setStockQuantity(20);


        ProductEntity savedProduct = new ProductEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        savedProduct.setId(id);
        savedProduct.setProductName("Filip AC300");
        savedProduct.setProductType("Electronic");
        savedProduct.setPrice(new BigDecimal("19.20"));
        savedProduct.setStockQuantity(20);

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedProduct);

        // Act
        ProductEntity result = productService.addProduct(request);

        // Assert
        assertEquals(savedProduct, result);
        verify(productRepository).save(any(ProductEntity.class));

        ArgumentCaptor<ProductEntity> captor =
                ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(captor.capture());
        ProductEntity captured = captor.getValue();

        assertEquals("Filip AC300", captured.getProductName());
        assertEquals("Electronic", captured.getProductType());
        assertEquals(new BigDecimal("19.20"), captured.getPrice());
        assertEquals(20, captured.getStockQuantity());
    }

    @Test
    void updateProduct_shouldReturnSuccessResponse(){
        //Finds the customer using the correct UUID.
        //Updates all five fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();
        request.setProductName("Filip AC300 Lite");
        request.setProductType("Electronic");
        request.setPrice(new BigDecimal("19.20"));
        request.setStockQuantity(20);

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(id);
        existingProduct.setProductName("Filip AC300");
        existingProduct.setProductType("Electronic");
        existingProduct.setPrice(new BigDecimal("19.20"));
        existingProduct.setStockQuantity(20);

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setId(id);
        savedProduct.setProductName("Filip AC300");
        savedProduct.setProductType("Electronic");
        savedProduct.setPrice(new BigDecimal("19.20"));
        savedProduct.setStockQuantity(20);

        when(productRepository.save(any(ProductEntity.class)))
                .thenReturn(savedProduct);

        when(productRepository.findById(id))
                .thenReturn(Optional.of(existingProduct));

        MessageResponse result = productService.updateProduct(id, request);

        // Assert
        assertEquals("Successfully updated 123e4567-e89b-12d3-a456-426614174000, Filip AC300 Lite", result.getMessage());

        ArgumentCaptor<ProductEntity> captor =
                ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(captor.capture());
        verify(productRepository).findById(id);
        ProductEntity captured = captor.getValue();

        assertEquals("Filip AC300 Lite", captured.getProductName());
        assertEquals("Electronic", captured.getProductType());
        assertEquals(new BigDecimal("19.20"), captured.getPrice());
        assertEquals(20, captured.getStockQuantity());
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();
        request.setProductName("Filip AC300");
        request.setProductType("Electronic");
        request.setPrice(new BigDecimal("19.20"));
        request.setStockQuantity(20);

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(id, request)
        );
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void patchProduct_shouldUpdateProvidedFieldsOnly(){
        //Finds the customer using the correct UUID.
        //Updates only fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();
        request.setProductName("Filip AC300 Lite");

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(id);
        existingProduct.setProductName("Filip AC300");
        existingProduct.setProductType("Electronic");
        existingProduct.setPrice(new BigDecimal("19.20"));
        existingProduct.setStockQuantity(20);

        when(productRepository.findById(id))
                .thenReturn(Optional.of(existingProduct));

        MessageResponse result = productService.patchProduct(id, request);

        // Assert
        assertEquals("Successfully patched 123e4567-e89b-12d3-a456-426614174000, Filip AC300 Lite", result.getMessage());

        ArgumentCaptor<ProductEntity> captor =
                ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).findById(id);
        verify(productRepository).save(captor.capture());
        ProductEntity captured = captor.getValue();

        assertEquals("Filip AC300 Lite", captured.getProductName());
        assertEquals("Electronic", captured.getProductType());
        assertEquals(new BigDecimal("19.20"), captured.getPrice());
        assertEquals(20, captured.getStockQuantity());
    }

    @Test
    void patchProduct_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        //Never performs save
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();
        request.setProductName("Janet");


        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.patchProduct(id, request)
        );
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void patchProduct_shouldUpdateNothingIfAllFieldsNull(){
        //Finds the customer using the correct UUID.
        //Updates only fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductRequest request = new ProductRequest();

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(id);
        existingProduct.setProductName("Filip AC300");
        existingProduct.setProductType("Electronic");
        existingProduct.setPrice(new BigDecimal("19.20"));
        existingProduct.setStockQuantity(20);

        when(productRepository.findById(id))
                .thenReturn(Optional.of(existingProduct));

        MessageResponse result = productService.patchProduct(id, request);

        // Assert
        assertEquals("Successfully patched 123e4567-e89b-12d3-a456-426614174000, Filip AC300", result.getMessage());

        ArgumentCaptor<ProductEntity> captor =
                ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).findById(id);
        verify(productRepository).save(captor.capture());
        ProductEntity captured = captor.getValue();

        assertEquals("Filip AC300", captured.getProductName());
        assertEquals("Electronic", captured.getProductType());
        assertEquals(new BigDecimal("19.20"), captured.getPrice());
        assertEquals(20, captured.getStockQuantity());
    }

    @Test
    void deleteProduct_shouldDeleteCustomerAndReturnMessage() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(id);
        existingProduct.setProductName("Filip AC300");
        existingProduct.setProductType("Electronic");
        existingProduct.setPrice(new BigDecimal("19.20"));
        existingProduct.setStockQuantity(20);


        MessageResponse expected =
                new MessageResponse("Successfully deleted 123e4567-e89b-12d3-a456-426614174000, Filip AC300");

        when(productRepository.findById(id))
                .thenReturn(Optional.of(existingProduct));

        // Act
        MessageResponse response = productService.deleteProduct(id);

        // Assert
        assertEquals(expected.getMessage(), response.getMessage());
        verify(productRepository).findById(id);
        verify(productRepository).delete(existingProduct);
    }

    @Test
    void deleteProduct_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        //Never performs delete.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(id)
        );
        verify(productRepository).findById(id);
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    void uploadProducts_shouldUploadSuccessfullyAndReturnMessage() throws IOException {
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.txt",
                "text/plain",
                "Filip AC300|Electronics|1299.90|5".getBytes()
        );

        MessageResponse response = productService.uploadProducts(file);

        ArgumentCaptor<List<ProductEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());
        List<ProductEntity> capturedList = captor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals("Filip AC300", capturedList.get(0).getProductName());
        assertEquals("Electronics", capturedList.get(0).getProductType());
        assertEquals(new BigDecimal("1299.90"), capturedList.get(0).getPrice());
        assertEquals(5, capturedList.get(0).getStockQuantity());
        assertEquals("Successfully uploaded data to DB", response.getMessage());
    }

    @Test
    void uploadProducts_shouldThrowException() throws IOException {
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.txt",
                "text/plain",
                new byte[0]
        );

        //Act and Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.uploadProducts(file)
        );

        //Assert
        verify(productRepository, never()).saveAll(any(List.class));
        assertEquals("Upload file is empty", exception.getMessage());
    }

    @Test
    void uploadProducts_shouldThrowExceptionWhenInvalidNumberOfFields() throws IOException {
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.txt",
                "text/plain",
                "Electronics|1299.90|5".getBytes()
        );

        //Act and Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.uploadProducts(file)
        );

        //Assert
        verify(productRepository, never()).saveAll(any(List.class));
        assertEquals("Invalid product data: expected 4 fields", exception.getMessage());
    }

    @Test
    void uploadProducts_shouldThrowExceptionWhenInvalidPrice() throws IOException {
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.txt",
                "text/plain",
                "Filip AC300|Electronics|abc|5".getBytes()
        );

        //Act and Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.uploadProducts(file)
        );

        //Assert
        verify(productRepository, never()).saveAll(any(List.class));
        assertEquals("Invalid product price: abc", exception.getMessage());
    }

    @Test
    void uploadProducts_shouldThrowExceptionWhenInvalidStockQuantity() throws IOException {
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.txt",
                "text/plain",
                "Filip AC300|Electronics|1299.90|abc".getBytes()
        );

        //Act and Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.uploadProducts(file)
        );

        //Assert
        verify(productRepository, never()).saveAll(any(List.class));
        assertEquals("Invalid stock quantity: abc", exception.getMessage());
    }

    @Test
    void uploadProducts_shouldThrowExceptionAndNotSaveWhenInvalidSecondRow() throws IOException {
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.txt",
                "text/plain",
                ("Filip AC300|Electronics|1299.90|5\n" + "Filip AC300 Pro|Electronics|3099.90|abc").getBytes()
        );

        //Act and Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.uploadProducts(file)
        );

        //Assert
        verify(productRepository, never()).saveAll(any(List.class));
        assertEquals("Invalid stock quantity: abc", exception.getMessage());
    }
}
