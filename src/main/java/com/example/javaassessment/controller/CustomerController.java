package com.example.javaassessment.controller;

import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.request.CustomerRequest;
import com.example.javaassessment.response.MessageResponse;
import com.example.javaassessment.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerEntity> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @PostMapping
    public ResponseEntity<CustomerEntity> addCustomer(
            @Valid @RequestBody CustomerRequest request
    ){
        logger.debug("[CustomerController] addCustomer: {}", request);
        CustomerEntity savedCustomer = customerService.addCustomer(request);

        return ResponseEntity
                .created(URI.create("/customer/" + savedCustomer.getId()))
                .body(savedCustomer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request
    ){
        logger.info("[CustomerController] updateCustomer: ID={}", id);
        logger.debug("[CustomerController] updateCustomer: {}", request);
        MessageResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> patchCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request
    ){
        logger.info("[CustomerController] patchCustomer: ID={}", id);
        logger.debug("[CustomerController] patchCustomer: {}", request);
        MessageResponse response = customerService.patchCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCustomer(
            @PathVariable UUID id
    ){
        logger.info("[CustomerController] deleteCustomer: ID={}", id);
        MessageResponse response = customerService.deleteCustomer(id);
        return ResponseEntity.ok(response);
    }
}
