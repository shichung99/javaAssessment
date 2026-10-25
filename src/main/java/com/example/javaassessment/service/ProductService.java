package com.example.javaassessment.service;

import com.example.javaassessment.controller.CustomerController;
import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.exception.BusinessException;
import com.example.javaassessment.exception.ResourceNotFoundException;
import com.example.javaassessment.repository.CustomerRepository;
import com.example.javaassessment.repository.ProductRepository;
import com.example.javaassessment.request.CustomerRequest;
import com.example.javaassessment.request.ProductRequest;
import com.example.javaassessment.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String PRODUCT_NOT_EXIST_EXCEPTION_MESSAGE = "Product does not exist";
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<ProductEntity> getAllProducts(){
        return productRepository.findAll();
    }

    public ProductEntity addProduct(ProductRequest request){
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(request.getProductName());
        productEntity.setProductType(request.getProductType());
        productEntity.setPrice(request.getPrice());
        productEntity.setStockQuantity(request.getStockQuantity());

        ProductEntity savedProduct = productRepository.save(productEntity);
        logger.info("[ProductService] added Product: ID={}", savedProduct.getId());
        return savedProduct;
    }

    public MessageResponse updateProduct(UUID id, ProductRequest request){
        ProductEntity productEntity = productRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(PRODUCT_NOT_EXIST_EXCEPTION_MESSAGE));

        productEntity.setProductName(request.getProductName());
        productEntity.setProductType(request.getProductType());
        productEntity.setPrice(request.getPrice());
        productEntity.setStockQuantity(request.getStockQuantity());

        productRepository.save(productEntity);
        return new MessageResponse("Successfully updated " +  productEntity.getId() + ", " + productEntity.getProductName());
    }

    public MessageResponse patchProduct(UUID id, ProductRequest request){
        ProductEntity productEntity = productRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(PRODUCT_NOT_EXIST_EXCEPTION_MESSAGE));

        if (request.getProductName() != null) {
            productEntity.setProductName(request.getProductName());
        }

        if (request.getProductType() != null) {
            productEntity.setProductType(request.getProductType());
        }

        if (request.getPrice() != null) {
            productEntity.setPrice(request.getPrice());
        }

        if (request.getStockQuantity() != null) {
            productEntity.setStockQuantity(request.getStockQuantity());
        }


        productRepository.save(productEntity);
        return new MessageResponse("Successfully patched " +  productEntity.getId() + ", " + productEntity.getProductName());
    }

    public MessageResponse deleteProduct(UUID id){
        ProductEntity productEntity = productRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(PRODUCT_NOT_EXIST_EXCEPTION_MESSAGE));

        productRepository.delete(productEntity);
        return new MessageResponse("Successfully deleted " + productEntity.getId() + ", " + productEntity.getProductName());
    }

    public MessageResponse uploadProducts(MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new BusinessException("Upload file is empty");
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            String line;
            BigDecimal price;
            Integer stockQuantity;
            List<ProductEntity> productEntityList = new ArrayList<>();

            while((line = reader.readLine()) != null){
                String[] fields = line.split("\\|");
                ProductEntity entity = new ProductEntity();
                if (logger.isDebugEnabled()) {
                    logger.debug("Product fields: {}", Arrays.toString(fields));
                }
                if (fields.length != 4) {
                    throw new BusinessException("Invalid product data: expected 4 fields");
                }
                //productName
                entity.setProductName(fields[0]);
                //productType
                entity.setProductType(fields[1]);
                //price
                try {
                    price = new BigDecimal(fields[2]);
                    entity.setPrice(price);
                } catch (NumberFormatException e) {
                    throw new BusinessException("Invalid product price: " + fields[2]);
                }
                //stockQuantity
                try {
                    stockQuantity = Integer.valueOf(fields[3]);
                    entity.setStockQuantity(stockQuantity);
                } catch (NumberFormatException e) {
                    throw new BusinessException("Invalid stock quantity: " + fields[3]);
                }

                productEntityList.add(entity);

            }
            productRepository.saveAll(productEntityList);
        }

        return new MessageResponse("Successfully uploaded data to DB");

    }
}
