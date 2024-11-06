package com.azienda.erp.erp_backend.controller;

import com.azienda.erp.erp_backend.entity.Supplier;
import com.azienda.erp.erp_backend.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per la gestione dei fornitori nell'applicazione ERP.
 * Espone vari endpoint per creare, recuperare, aggiornare e cancellare fornitori.
 */
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Recupera tutti i fornitori presenti nel sistema.
     *
     * @return Lista di tutti i fornitori.
     */
    @Operation(summary = "Recupera tutti i fornitori", description = "Endpoint per ottenere la lista di tutti i fornitori presenti nel sistema.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Lista dei fornitori recuperata con successo",
            content = @Content(schema = @Schema(implementation = Supplier.class)))
    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    /**
     * Crea un nuovo fornitore.
     *
     * @param supplier Dettagli del fornitore da creare.
     * @return Il nuovo fornitore creato.
     */
    @Operation(summary = "Crea un nuovo fornitore", description = "Endpoint per creare un nuovo fornitore.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornitore creato con successo",
                    content = @Content(schema = @Schema(implementation = Supplier.class))),
            @ApiResponse(responseCode = "400", description = "Errore durante la creazione del fornitore",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) {
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }


    /**
     * Recupera un fornitore specifico tramite ID.
     *
     * @param id L'ID del fornitore da recuperare.
     * @return Il fornitore corrispondente all'ID fornito.
     */
    @Operation(summary = "Recupera un fornitore per ID", description = "Endpoint per ottenere un fornitore specifico tramite ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornitore trovato",
                    content = @Content(schema = @Schema(implementation = Supplier.class))),
            @ApiResponse(responseCode = "404", description = "Fornitore non trovato",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    /**
     * Aggiorna le informazioni di un fornitore esistente.
     *
     * @param id L'ID del fornitore da aggiornare.
     * @param supplierDetails I nuovi dettagli del fornitore.
     * @return Il fornitore aggiornato.
     */
    @Operation(summary = "Aggiorna un fornitore esistente", description = "Endpoint per aggiornare le informazioni di un fornitore esistente.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornitore aggiornato con successo",
                    content = @Content(schema = @Schema(implementation = Supplier.class))),
            @ApiResponse(responseCode = "404", description = "Fornitore non trovato",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody Supplier supplierDetails) {
        Supplier updatedSupplier = supplierService.updateSupplier(id, supplierDetails);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * Cancella un fornitore a partire dal suo ID.
     *
     * @param id L'ID del fornitore da cancellare.
     * @return ResponseEntity senza contenuto se l'operazione ha successo.
     */
    @Operation(summary = "Cancella un fornitore", description = "Endpoint per cancellare un fornitore a partire dal suo ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fornitore cancellato con successo",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Fornitore non trovato",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
