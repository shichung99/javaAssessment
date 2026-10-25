package com.example.javaassessment.controller;

import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.request.CustomerRequest;
import com.example.javaassessment.response.MessageResponse;
import com.example.javaassessment.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void getAllCustomers_shouldReturnAllCustomers() {
        // Arrange
        List<CustomerEntity> customers = List.of(
                new CustomerEntity(),
                new CustomerEntity()
        );

        when(customerService.getAllCustomers())
                .thenReturn(customers);

        // Act
        List<CustomerEntity> result =
                customerController.getAllCustomers();

        // Assert
        assertEquals(customers, result);
        verify(customerService).getAllCustomers();
    }

    @Test
    void getAllCustomers_shouldReturnEmptyList() {
        // Arrange
        when(customerService.getAllCustomers())
                .thenReturn(List.of());

        // Act
        List<CustomerEntity> result =
                customerController.getAllCustomers();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void addCustomers_shouldReturnCreatedCustomerWithLocation() {
        // Arrange
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPersonalEmail("janedoe@email.com");
        request.setBusinessEmail("janedoe@email.com");
        request.setContactNo("123456789");

        CustomerEntity savedCustomer = new CustomerEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        savedCustomer.setId(id);
        savedCustomer.setFirstName("Jane");
        savedCustomer.setLastName("Doe");
        savedCustomer.setPersonalEmail("janedoe@email.com");
        savedCustomer.setBusinessEmail("janedoe@email.com");
        savedCustomer.setContactNo("123456789");

        when(customerService.addCustomer(request))
                .thenReturn(savedCustomer);

        // Act
        ResponseEntity<?> response = customerController.addCustomer(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedCustomer, response.getBody());
        assertEquals(URI.create("/customer/" + id), response.getHeaders().getLocation());
    }

    @Test
    void updateCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPersonalEmail("janedoe@email.com");
        request.setBusinessEmail("janedoe@email.com");
        request.setContactNo("123456789");

        MessageResponse expected =
                new MessageResponse("Successfully updated Jane Doe");

        when(customerService.updateCustomer(id, request))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = customerController.updateCustomer(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully updated Jane Doe", ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void patchCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPersonalEmail("janedoe123@email.com");

        MessageResponse expected =
                new MessageResponse("Successfully patched Jane Doe");

        when(customerService.patchCustomer(id, request))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = customerController.patchCustomer(id, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully patched Jane Doe", ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void deleteCustomer_shouldReturnOk() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        MessageResponse expected =
                new MessageResponse("Successfully deleted Jane Doe");

        when(customerService.deleteCustomer(id))
                .thenReturn(expected);
        // Act
        ResponseEntity<?> response = customerController.deleteCustomer(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully deleted Jane Doe", ((MessageResponse)response.getBody()).getMessage());
    }
}

