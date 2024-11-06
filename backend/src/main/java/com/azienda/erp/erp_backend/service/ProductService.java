package com.azienda.erp.erp_backend.service;

import com.azienda.erp.erp_backend.entity.Product;
import com.azienda.erp.erp_backend.entity.Supplier;
import com.azienda.erp.erp_backend.exception.ProductNotFoundException;
import com.azienda.erp.erp_backend.exception.SupplierNotFoundException;
import com.azienda.erp.erp_backend.repository.ProductRepository;
import com.azienda.erp.erp_backend.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servizio per la gestione dei prodotti.
 * Fornisce operazioni CRUD per i prodotti e interagisce con il repository per salvare e recuperare i dati.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    /**
     * Recupera la lista di tutti i prodotti.
     *
     * @return lista di tutti i prodotti presenti nel database.
     */
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    /**
     * Cerca un prodotto tramite il suo barcode.
     *
     * @param barcode il codice a barre del prodotto da cercare.
     * @return il prodotto trovato.
     * @throws ProductNotFoundException se il prodotto non viene trovato.
     */
    public Product findByBarcode(Long barcode) {
        Product product = productRepository.findByBarcode(barcode);
        if (product == null) {
            throw new ProductNotFoundException("Prodotto con barcode " + barcode + " non trovato");
        }
        return product;
    }

    /**
     * Cerca un prodotto tramite il suo ID.
     *
     * @param id l'ID del prodotto da cercare.
     * @return il prodotto trovato.
     * @throws ProductNotFoundException se il prodotto con l'ID specificato non viene trovato.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + id + " non trovato"));
    }

    /**
     * Crea un nuovo prodotto nel database.
     *
     * @param product il prodotto da creare.
     * @return il prodotto creato.
     * @throws DataIntegrityViolationException se il barcode del prodotto non è unico.
     * @throws SupplierNotFoundException se il fornitore specificato non viene trovato.
     */
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.findByBarcode(product.getBarcode()) != null) {
            throw new DataIntegrityViolationException("Il barcode deve essere unico!");
        }

        if (product.getSellingPrice() < product.getPurchasePrice()) {
            throw new IllegalArgumentException("Il prezzo di vendita non può essere inferiore al prezzo di acquisto");
        }

        Supplier supplier = supplierRepository.findById(product.getSupplier().getId())
                .orElseThrow(() -> new SupplierNotFoundException("Fornitore non trovato"));
        product.setSupplier(supplier);
        return productRepository.save(product);
    }

    /**
     * Aggiorna un prodotto esistente.
     *
     * @param id l'ID del prodotto da aggiornare.
     * @param productDetails i dettagli aggiornati del prodotto.
     * @return il prodotto aggiornato.
     * @throws ProductNotFoundException se il prodotto con l'ID specificato non viene trovato.
     * @throws SupplierNotFoundException se il fornitore specificato non viene trovato.
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + id + " non trovato"));

        if (productDetails.getSellingPrice() < productDetails.getPurchasePrice()) {
            throw new IllegalArgumentException("Il prezzo di vendita non può essere inferiore al prezzo di acquisto");
        }

        product.setBarcode(productDetails.getBarcode());
        product.setName(productDetails.getName());
        product.setQuantity(productDetails.getQuantity());
        product.setPurchasePrice(productDetails.getPurchasePrice());
        product.setSellingPrice(productDetails.getSellingPrice());

        Supplier supplier = supplierRepository.findById(productDetails.getSupplier().getId())
                .orElseThrow(() -> new SupplierNotFoundException("Fornitore non trovato"));
        product.setSupplier(supplier);

        return productRepository.save(product);
    }

    /**
     * Elimina un prodotto dal database.
     *
     * @param id l'ID del prodotto da eliminare.
     * @throws ProductNotFoundException se il prodotto con l'ID specificato non viene trovato.
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + id + " non trovato"));
        productRepository.delete(product);
    }
}