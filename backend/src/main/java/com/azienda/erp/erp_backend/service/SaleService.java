package com.azienda.erp.erp_backend.service;

import com.azienda.erp.erp_backend.entity.Product;
import com.azienda.erp.erp_backend.entity.Sale;
import com.azienda.erp.erp_backend.entity.SaleItem;
import com.azienda.erp.erp_backend.exception.SaleNotFoundException;
import com.azienda.erp.erp_backend.exception.ProductNotFoundException;
import com.azienda.erp.erp_backend.exception.InsufficientProductQuantityException;
import com.azienda.erp.erp_backend.repository.ProductRepository;
import com.azienda.erp.erp_backend.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }


    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public List<Sale> orderByDesc() {
        return saleRepository.findAllByOrderBySaleDateDesc();
    }

    public List<Sale> getTop12Sales() {
        return saleRepository.findTop12ByOrderBySaleDateDesc();
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Vendita con ID " + id + " non trovata."));
    }

    public List<Sale> getSalesByDate(LocalDate date) {
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault());
        return saleRepository.findBySaleDateBetween(startOfDay, endOfDay);
    }

    public List<Sale> getSalesByCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        ZonedDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault());
        return saleRepository.findBySaleDateBetween(startOfMonth, endOfMonth);
    }


    @Transactional
    public Sale createSale(Sale sale) {
        double totalPrice = 0;
        double netProfit = 0;
        long totalProducts = 0;

        Sale newSale = new Sale();
        newSale.setSaleItems(new ArrayList<>());

        for (SaleItem saleItem : sale.getSaleItems()) {
            Product product = productRepository.findByBarcode(saleItem.getProduct().getBarcode());
            if (product == null) {
                throw new ProductNotFoundException("Prodotto con barcode " + saleItem.getProduct().getBarcode() + " non trovato.");
            }

            if (product.getQuantity() < saleItem.getQuantitySold()) {
                throw new InsufficientProductQuantityException("Quantità venduta superiore alla disponibilità del prodotto " + product.getBarcode());
            }

            saleItem.setPurchasePrice(product.getPurchasePrice() * saleItem.getQuantitySold());
            saleItem.setSellingPrice(product.getSellingPrice() * saleItem.getQuantitySold());
            saleItem.setProduct(product);
            saleItem.setSale(newSale);

            product.setQuantity(product.getQuantity() - saleItem.getQuantitySold());
            productRepository.save(product);

            totalPrice += saleItem.getSellingPrice();
            netProfit += (saleItem.getSellingPrice() - saleItem.getPurchasePrice());
            totalProducts += saleItem.getQuantitySold();
            newSale.getSaleItems().add(saleItem);
        }

        newSale.setSaleDate(ZonedDateTime.now());
        newSale.setDiscount(sale.getDiscount());
        newSale.setPaymentMethods(sale.getPaymentMethods());
        newSale.setTotalProducts(totalProducts);
        newSale.setNote(sale.getNote());
        newSale.setTotalPrice(totalPrice - sale.getDiscount());
        newSale.setNetProfit(netProfit - sale.getDiscount());
        return saleRepository.save(newSale);
    }

    @Transactional
    public Sale updateSale(Long saleId, Sale saleDetails) {
        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException("Vendita con ID " + saleId + " non trovata."));

        existingSale.setDiscount(saleDetails.getDiscount());
        existingSale.setPaymentMethods(saleDetails.getPaymentMethods());
        existingSale.setNote(saleDetails.getNote());

        List<SaleItem> existingSaleItems = existingSale.getSaleItems();
        List<SaleItem> updatedSaleItems = new ArrayList<>();

        for (SaleItem existingItem : existingSaleItems) {
            boolean stillExists = saleDetails.getSaleItems().stream()
                    .anyMatch(newItem -> newItem.getProduct().getBarcode().equals(existingItem.getProduct().getBarcode()));

            if (!stillExists) {
                Product product = existingItem.getProduct();
                product.setQuantity(product.getQuantity() + existingItem.getQuantitySold());
                productRepository.save(product);
            } else {
                updatedSaleItems.add(existingItem);
            }
        }

        double totalPrice = 0;
        double netProfit = 0;
        long totalProducts = 0;

        for (SaleItem saleItemDetails : saleDetails.getSaleItems()) {
            Product product = productRepository.findByBarcode(saleItemDetails.getProduct().getBarcode());
            if (product == null) {
                throw new ProductNotFoundException("Prodotto con barcode " + saleItemDetails.getProduct().getBarcode() + " non trovato.");
            }

            SaleItem existingSaleItem = existingSaleItems.stream()
                    .filter(item -> item.getProduct().getBarcode().equals(saleItemDetails.getProduct().getBarcode()))
                    .findFirst()
                    .orElse(null);

            if (existingSaleItem != null) {
                product.setQuantity(product.getQuantity() + existingSaleItem.getQuantitySold());
                existingSaleItem.setQuantitySold(saleItemDetails.getQuantitySold());
                existingSaleItem.setPurchasePrice(product.getPurchasePrice() * saleItemDetails.getQuantitySold());
                existingSaleItem.setSellingPrice(product.getSellingPrice() * saleItemDetails.getQuantitySold());
                product.setQuantity(product.getQuantity() - saleItemDetails.getQuantitySold());
                updatedSaleItems.add(existingSaleItem);
            } else {
                SaleItem newSaleItem = new SaleItem();
                newSaleItem.setProduct(product);
                newSaleItem.setQuantitySold(saleItemDetails.getQuantitySold());
                newSaleItem.setPurchasePrice(product.getPurchasePrice() * saleItemDetails.getQuantitySold());
                newSaleItem.setSellingPrice(product.getSellingPrice() * saleItemDetails.getQuantitySold());
                newSaleItem.setSale(existingSale);
                product.setQuantity(product.getQuantity() - saleItemDetails.getQuantitySold());
                updatedSaleItems.add(newSaleItem);
            }

            productRepository.save(product);
            totalPrice += saleItemDetails.getSellingPrice() * saleItemDetails.getQuantitySold();
            netProfit += (saleItemDetails.getSellingPrice() - saleItemDetails.getPurchasePrice()) * saleItemDetails.getQuantitySold();
            totalProducts += saleItemDetails.getQuantitySold();
        }

        existingSale.getSaleItems().clear();
        existingSale.getSaleItems().addAll(updatedSaleItems);

        existingSale.setTotalPrice(totalPrice - saleDetails.getDiscount());
        existingSale.setNetProfit(netProfit - saleDetails.getDiscount());
        existingSale.setTotalProducts(totalProducts);

        return saleRepository.save(existingSale);
    }

    @Transactional
    public void deleteSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new SaleNotFoundException("Vendita con ID " + saleId + " non trovata."));

        for (SaleItem saleItem : sale.getSaleItems()) {
            Product product = saleItem.getProduct();
            product.setQuantity(product.getQuantity() + saleItem.getQuantitySold());
            productRepository.save(product);
        }

        saleRepository.delete(sale);
    }
}
