package com.example.javaassessment.service;

import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.exception.ResourceNotFoundException;
import com.example.javaassessment.repository.CustomerRepository;
import com.example.javaassessment.request.CustomerRequest;
import com.example.javaassessment.response.MessageResponse;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private static final String CUSTOMER_NOT_EXIST_EXCEPTION_MESSAGE = "Customer does not exist";

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public List<CustomerEntity> getAllCustomers(){
        return customerRepository.findAll();
    }

    public CustomerEntity addCustomer(CustomerRequest request){
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(request.getFirstName());
        customerEntity.setLastName(request.getLastName());
        customerEntity.setBusinessEmail(request.getBusinessEmail());
        customerEntity.setPersonalEmail(request.getPersonalEmail());
        customerEntity.setContactNo(request.getContactNo());

        CustomerEntity savedCustomer = customerRepository.save(customerEntity);
        logger.info("[ProductService] added Product: ID={}", savedCustomer.getId());
        return savedCustomer;
    }

    public MessageResponse updateCustomer(UUID id, CustomerRequest request){
        CustomerEntity customerEntity = customerRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(CUSTOMER_NOT_EXIST_EXCEPTION_MESSAGE));

        customerEntity.setFirstName(request.getFirstName());
        customerEntity.setLastName(request.getLastName());
        customerEntity.setBusinessEmail(request.getBusinessEmail());
        customerEntity.setPersonalEmail(request.getPersonalEmail());
        customerEntity.setContactNo(request.getContactNo());

        customerRepository.save(customerEntity);
        return new MessageResponse("Successfully updated " + customerEntity.getFirstName() + " " + customerEntity.getLastName());
    }

    public MessageResponse patchCustomer(UUID id, CustomerRequest request){
        CustomerEntity customerEntity = customerRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(CUSTOMER_NOT_EXIST_EXCEPTION_MESSAGE));

        if (request.getFirstName() != null) {
            customerEntity.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            customerEntity.setLastName(request.getLastName());
        }

        if (request.getBusinessEmail() != null) {
            customerEntity.setBusinessEmail(request.getBusinessEmail());
        }

        if (request.getPersonalEmail() != null) {
            customerEntity.setPersonalEmail(request.getPersonalEmail());
        }

        if (request.getContactNo() != null) {
            customerEntity.setContactNo(request.getContactNo());
        }

        customerRepository.save(customerEntity);
        return new MessageResponse("Successfully patched " + customerEntity.getFirstName() + " " + customerEntity.getLastName());
    }

    public MessageResponse deleteCustomer(UUID id){
        CustomerEntity customerEntity = customerRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(CUSTOMER_NOT_EXIST_EXCEPTION_MESSAGE));

        customerRepository.delete(customerEntity);
        return new MessageResponse("Successfully deleted " + customerEntity.getFirstName() + " " + customerEntity.getLastName());
    }
}
