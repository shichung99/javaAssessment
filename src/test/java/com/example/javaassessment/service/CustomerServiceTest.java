package com.example.javaassessment.service;

import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.exception.ResourceNotFoundException;
import com.example.javaassessment.repository.CustomerRepository;
import com.example.javaassessment.request.CustomerRequest;
import com.example.javaassessment.response.MessageResponse;
import org.aspectj.bridge.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getAllCustomers_shouldReturnList(){
        // Arrange
        List<CustomerEntity> customers = List.of(
                new CustomerEntity(),
                new CustomerEntity()
        );

        when(customerRepository.findAll())
                .thenReturn(customers);

        // Act
        List<CustomerEntity> result =
                customerService.getAllCustomers();

        // Assert
        assertEquals(customers, result);
        verify(customerRepository).findAll();
    }

    @Test
    void getAllCustomers_shouldReturnEmptyList() {
        // Arrange
        when(customerRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<CustomerEntity> result =
                customerService.getAllCustomers();

        // Assert
        assertTrue(result.isEmpty());
        verify(customerRepository).findAll();
    }

    @Test
    void addCustomers_shouldReturnCreatedCustomer() {
        // Arrange
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPersonalEmail("janedoe@email.com");
        request.setBusinessEmail("janedoe@email.com");
        request.setContactNo("123456789");

        CustomerEntity toBeSavedCustomer = new CustomerEntity();
        toBeSavedCustomer.setFirstName("Jane");
        toBeSavedCustomer.setLastName("Doe");
        toBeSavedCustomer.setPersonalEmail("janedoe@email.com");
        toBeSavedCustomer.setBusinessEmail("janedoe@email.com");
        toBeSavedCustomer.setContactNo("123456789");

        CustomerEntity savedCustomer = new CustomerEntity();
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        savedCustomer.setId(id);
        savedCustomer.setFirstName("Jane");
        savedCustomer.setLastName("Doe");
        savedCustomer.setPersonalEmail("janedoe@email.com");
        savedCustomer.setBusinessEmail("janedoe@email.com");
        savedCustomer.setContactNo("123456789");

        when(customerRepository.save(any(CustomerEntity.class)))
                .thenReturn(savedCustomer);

        // Act
        CustomerEntity result = customerService.addCustomer(request);

        // Assert
        assertEquals(savedCustomer, result);
        verify(customerRepository).save(any(CustomerEntity.class));

        ArgumentCaptor<CustomerEntity> captor =
                ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).save(captor.capture());
        CustomerEntity captured = captor.getValue();

        assertEquals("Jane", captured.getFirstName());
        assertEquals("Doe", captured.getLastName());
        assertEquals("janedoe@email.com", captured.getPersonalEmail());
        assertEquals("janedoe@email.com", captured.getBusinessEmail());
        assertEquals("123456789", captured.getContactNo());
    }

    @Test
    void updateCustomer_shouldReturnSuccessResponse(){
        //Finds the customer using the correct UUID.
        //Updates all five fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPersonalEmail("janedoe@email.com");
        request.setBusinessEmail("janedoe@email.com");
        request.setContactNo("123456789");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setId(id);
        existingCustomer.setFirstName("Jane");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPersonalEmail("janedoe@email.com");
        existingCustomer.setBusinessEmail("janedoe@email.com");
        existingCustomer.setContactNo("123456789");

        CustomerEntity savedCustomer = new CustomerEntity();
        savedCustomer.setId(id);
        savedCustomer.setFirstName("Jane");
        savedCustomer.setLastName("Doe");
        savedCustomer.setPersonalEmail("janedoe@email.com");
        savedCustomer.setBusinessEmail("janedoe@email.com");
        savedCustomer.setContactNo("123456789");

        when(customerRepository.save(any(CustomerEntity.class)))
                .thenReturn(savedCustomer);

        when(customerRepository.findById(id))
                .thenReturn(Optional.of(existingCustomer));

        MessageResponse result = customerService.updateCustomer(id, request);

        // Assert
        assertEquals("Successfully updated Jane Doe", result.getMessage());

        ArgumentCaptor<CustomerEntity> captor =
                ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).save(captor.capture());
        verify(customerRepository).findById(id);
        CustomerEntity captured = captor.getValue();

        assertEquals("Jane", captured.getFirstName());
        assertEquals("Doe", captured.getLastName());
        assertEquals("janedoe@email.com", captured.getPersonalEmail());
        assertEquals("janedoe@email.com", captured.getBusinessEmail());
        assertEquals("123456789", captured.getContactNo());
    }

    @Test
    void updateCustomer_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPersonalEmail("janedoe@email.com");
        request.setBusinessEmail("janedoe@email.com");
        request.setContactNo("123456789");

        when(customerRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.updateCustomer(id, request)
        );
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void patchCustomer_shouldUpdateProvidedFieldsOnly(){
        //Finds the customer using the correct UUID.
        //Updates only fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Janet");

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setId(id);
        existingCustomer.setFirstName("Jane");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPersonalEmail("janedoe@email.com");
        existingCustomer.setBusinessEmail("janedoe@email.com");
        existingCustomer.setContactNo("123456789");

        when(customerRepository.findById(id))
                .thenReturn(Optional.of(existingCustomer));

        MessageResponse result = customerService.patchCustomer(id, request);

        // Assert
        assertEquals("Successfully patched Janet Doe", result.getMessage());

        ArgumentCaptor<CustomerEntity> captor =
                ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).findById(id);
        verify(customerRepository).save(captor.capture());
        CustomerEntity captured = captor.getValue();

        assertEquals("Janet", captured.getFirstName());
        assertEquals("Doe", captured.getLastName());
        assertEquals("janedoe@email.com", captured.getPersonalEmail());
        assertEquals("janedoe@email.com", captured.getBusinessEmail());
        assertEquals("123456789", captured.getContactNo());
    }

    @Test
    void patchCustomer_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        //Never performs save
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Janet");


        when(customerRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.patchCustomer(id, request)
        );
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
    }

    @Test
    void patchCustomer_shouldUpdateNothingIfAllFieldsNull(){
        //Finds the customer using the correct UUID.
        //Updates only fields from the request.
        //Saves the updated entity.
        //Returns the expected success message.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerRequest request = new CustomerRequest();

        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setId(id);
        existingCustomer.setFirstName("Jane");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPersonalEmail("janedoe@email.com");
        existingCustomer.setBusinessEmail("janedoe@email.com");
        existingCustomer.setContactNo("123456789");

        when(customerRepository.findById(id))
                .thenReturn(Optional.of(existingCustomer));

        MessageResponse result = customerService.patchCustomer(id, request);

        // Assert
        assertEquals("Successfully patched Jane Doe", result.getMessage());

        ArgumentCaptor<CustomerEntity> captor =
                ArgumentCaptor.forClass(CustomerEntity.class);
        verify(customerRepository).findById(id);
        verify(customerRepository).save(captor.capture());
        CustomerEntity captured = captor.getValue();

        assertEquals("Jane", captured.getFirstName());
        assertEquals("Doe", captured.getLastName());
        assertEquals("janedoe@email.com", captured.getPersonalEmail());
        assertEquals("janedoe@email.com", captured.getBusinessEmail());
        assertEquals("123456789", captured.getContactNo());
    }

    @Test
    void deleteCustomer_shouldDeleteCustomerAndReturnMessage() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setId(id);
        existingCustomer.setFirstName("Jane");
        existingCustomer.setLastName("Doe");
        existingCustomer.setPersonalEmail("janedoe@email.com");
        existingCustomer.setBusinessEmail("janedoe@email.com");
        existingCustomer.setContactNo("123456789");


        MessageResponse expected =
                new MessageResponse("Successfully deleted Jane Doe");

        when(customerRepository.findById(id))
                .thenReturn(Optional.of(existingCustomer));

        // Act
        MessageResponse response = customerService.deleteCustomer(id);

        // Assert
        assertEquals("Successfully deleted Jane Doe", response.getMessage());
        verify(customerRepository).findById(id);
        verify(customerRepository).delete(existingCustomer);
    }

    @Test
    void deleteCustomer_shouldThrowResourceNotFoundException(){
        //Throws the expected exception.
        //Never performs delete.
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(customerRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(id)
        );
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).delete(any(CustomerEntity.class));
    }
}
