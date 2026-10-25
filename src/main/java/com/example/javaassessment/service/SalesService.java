package com.example.javaassessment.service;

import com.example.javaassessment.entity.ProductEntity;
import com.example.javaassessment.entity.SalesEntity;
import com.example.javaassessment.exception.ResourceNotFoundException;
import com.example.javaassessment.repository.ProductRepository;
import com.example.javaassessment.repository.SalesRepository;
import com.example.javaassessment.request.ProductRequest;
import com.example.javaassessment.request.SalesRequest;
import com.example.javaassessment.response.MessageResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SalesService {
    private final SalesRepository salesRepository;
    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);
    private static final String SALES_NOT_EXIST_EXCEPTION_MESSAGE = "Sales does not exist";


    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public List<SalesEntity> getAllSales(){
        return salesRepository.findAll();
    }

    public SalesEntity addSales(SalesRequest request){
        SalesEntity salesEntity = new SalesEntity();
        salesEntity.setProductId(request.getProductId());
        salesEntity.setSalesDate(request.getSalesDate());
        salesEntity.setSalesPrice(request.getSalesPrice());
        salesEntity.setSalesQuantity(request.getSalesQuantity());

        SalesEntity savedSales = salesRepository.save(salesEntity);
        logger.info("[SalesService] added Sales: ID={}", savedSales.getId());
        return savedSales;
    }

    public MessageResponse updateSales(UUID id, SalesRequest request){
        SalesEntity salesEntity = salesRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(SALES_NOT_EXIST_EXCEPTION_MESSAGE));

        salesEntity.setProductId(request.getProductId());
        salesEntity.setSalesDate(request.getSalesDate());
        salesEntity.setSalesPrice(request.getSalesPrice());
        salesEntity.setSalesQuantity(request.getSalesQuantity());

        salesRepository.save(salesEntity);
        return new MessageResponse("Successfully updated " +  salesEntity.getId());
    }

    public MessageResponse patchSales(UUID id, SalesRequest request){
        SalesEntity salesEntity = salesRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(SALES_NOT_EXIST_EXCEPTION_MESSAGE));

        if (request.getProductId() != null) {
            salesEntity.setProductId(request.getProductId());
        }

        if (request.getSalesDate() != null) {
            salesEntity.setSalesDate(request.getSalesDate());
        }

        if (request.getSalesPrice() != null) {
            salesEntity.setSalesPrice(request.getSalesPrice());
        }

        if (request.getSalesQuantity() != null) {
            salesEntity.setSalesQuantity(request.getSalesQuantity());
        }


        salesRepository.save(salesEntity);
        return new MessageResponse("Successfully patched " +  salesEntity.getId());
    }

    public MessageResponse deleteSales(UUID id){
        SalesEntity salesEntity = salesRepository
                .findById(id)
                .orElseThrow(() ->new ResourceNotFoundException(SALES_NOT_EXIST_EXCEPTION_MESSAGE));

        salesRepository.delete(salesEntity);
        return new MessageResponse("Successfully deleted " + salesEntity.getId());
    }

    public byte[] generateSalesReport(LocalDate salesDate) throws JRException {
        List<SalesEntity> sales = salesRepository.findBySalesDate(salesDate);

        if (sales.isEmpty()) {
            throw new ResourceNotFoundException("No sales records found");
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportDate", salesDate);
        parameters.put("reportTitle", "Daily_Sales_Report_");
        JRBeanCollectionDataSource dataSource =
                new JRBeanCollectionDataSource(sales);

        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/sales_report.jrxml")
        );

        JasperPrint print = JasperFillManager.fillReport(
                jasperReport,
                parameters,
                dataSource
        );

        return JasperExportManager.exportReportToPdf(print);

    }

}
