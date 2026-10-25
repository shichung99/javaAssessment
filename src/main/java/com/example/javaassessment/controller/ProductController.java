package com.example.javaassessment.controller;

import com.example.javaassessment.entity.CustomerEntity;
import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.request.CustomerRequest;
import com.example.javaassessment.request.ProductRequest;
import com.example.javaassessment.response.MessageResponse;
import com.example.javaassessment.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductEntity> getAllProducts(){
        return productService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<ProductEntity> addProduct(
            @RequestBody ProductRequest request
    ){
        logger.debug("[ProductController] addProduct: {}", request);
        ProductEntity savedProduct = productService.addProduct(request);

        return ResponseEntity
                .created(URI.create("/product/" + savedProduct.getId()))
                .body(savedProduct);
    }

    @PutMapping
    public ResponseEntity<MessageResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequest request
    ){
        logger.info("[ProductController] updateProduct: ID={}", id);
        logger.debug("[ProductController] updateProduct: {}", request);
        MessageResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> patchProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequest request
    ){
        logger.info("[ProductController] patchProduct: ID={}", id);
        logger.debug("[ProductController] patchProduct: {}", request);
        MessageResponse response = productService.patchProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(
            @PathVariable UUID id
    ){
        logger.info("[ProductController] deleteProduct: ID={}", id);
        MessageResponse response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/products/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MessageResponse> uploadProduct(
            @RequestParam("file")MultipartFile file
    ) throws IOException {
        MessageResponse response = productService.uploadProducts(file);

        return ResponseEntity.ok(response);
    }

}
