package com.azienda.erp.erp_backend.service;

import com.azienda.erp.erp_backend.entity.Supplier;
import com.azienda.erp.erp_backend.exception.SupplierNotFoundException;
import com.azienda.erp.erp_backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servizio per la gestione dei fornitori.
 * Fornisce operazioni CRUD per le entità di tipo Supplier.
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    /**
     * Recupera tutti i fornitori presenti nel sistema.
     *
     * @return lista di tutti i fornitori.
     */
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    /**
     * Crea un nuovo fornitore.
     *
     * @param supplier il fornitore da creare.
     * @return il fornitore creato.
     */
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    /**
     * Recupera un fornitore tramite il suo ID.
     *
     * @param id l'ID del fornitore da recuperare.
     * @return il fornitore trovato.
     * @throws SupplierNotFoundException se il fornitore non viene trovato.
     */
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Fornitore con ID " + id + " non trovato."));
    }

    /**
     * Aggiorna un fornitore esistente.
     *
     * @param id l'ID del fornitore da aggiornare.
     * @param supplierDetails i nuovi dettagli del fornitore.
     * @return il fornitore aggiornato.
     * @throws SupplierNotFoundException se il fornitore non viene trovato.
     */
    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        Supplier supplier = getSupplierById(id);

        supplier.setName(supplierDetails.getName());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setPhoneNumber(supplierDetails.getPhoneNumber());

        return supplierRepository.save(supplier);
    }

    /**
     * Cancella un fornitore tramite il suo ID.
     *
     * @param id l'ID del fornitore da cancellare.
     * @throws SupplierNotFoundException se il fornitore non viene trovato.
     */
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }
}
