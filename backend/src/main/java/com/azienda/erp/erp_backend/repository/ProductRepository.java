package com.azienda.erp.erp_backend.repository;

import com.azienda.erp.erp_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Trova un prodotto tramite il suo barcode.
     *
     * @param barcode il codice a barre da cercare.
     * @return il prodotto con il barcode specificato, o null se non esiste alcun prodotto.
     */
    Product findByBarcode(Long barcode);
}