package com.azienda.erp.erp_backend.controller;

import com.azienda.erp.erp_backend.entity.Sale;
import com.azienda.erp.erp_backend.service.SaleService;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controller per la gestione delle vendite nell'applicazione ERP.
 * Espone vari endpoint per creare, recuperare, aggiornare e cancellare le vendite.
 */
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * Recupera tutte le vendite registrate.
     *
     * @return Lista di tutte le vendite.
     */
    @Operation(summary = "Recupera tutte le vendite", description = "Endpoint per ottenere tutte le vendite registrate.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Lista delle vendite recuperata con successo",
            content = @Content(schema = @Schema(implementation = Sale.class)))
    @GetMapping
    public List<Sale> getAllSales() {
        return saleService.getAllSales();
    }

    /**
     * Recupera una vendita specifica tramite ID.
     *
     * @param id L'ID della vendita da recuperare.
     * @return La vendita corrispondente all'ID fornito.
     */
    @Operation(summary = "Recupera una vendita per ID", description = "Endpoint per ottenere una vendita specifica tramite ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendita trovata",
                    content = @Content(schema = @Schema(implementation = Sale.class))),
            @ApiResponse(responseCode = "404", description = "Vendita non trovata",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Sale sale = saleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }

    /**
     * Recupera tutte le vendite ordinate in ordine decrescente.
     *
     * @return Lista delle vendite in ordine decrescente.
     */
    @Operation(summary = "Recupera tutte le vendite in ordine decrescente", description = "Endpoint per ottenere tutte le vendite ordinate in ordine decrescente.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Vendite ordinate in ordine decrescente recuperate con successo",
            content = @Content(schema = @Schema(implementation = Sale.class)))
    @GetMapping("/orderByDesc")
    public ResponseEntity<List<Sale>> getOrderByDesc() {
        List<Sale> sales = saleService.orderByDesc();
        return ResponseEntity.ok(sales);
    }

    /**
     * Recupera le ultime 12 vendite registrate.
     *
     * @return Lista delle ultime 12 vendite.
     */
    @Operation(summary = "Recupera le ultime 12 vendite", description = "Endpoint per ottenere le ultime 12 vendite registrate.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Ultime 12 vendite recuperate con successo",
            content = @Content(schema = @Schema(implementation = Sale.class)))
    @GetMapping("/latest")
    public ResponseEntity<List<Sale>> getTop12Sales() {
        List<Sale> topSales = saleService.getTop12Sales();
        return ResponseEntity.ok(topSales);
    }

    /**
     * Recupera tutte le vendite effettuate oggi.
     *
     * @return Lista delle vendite di oggi.
     */
    @Operation(summary = "Recupera le vendite di oggi", description = "Endpoint per ottenere tutte le vendite effettuate oggi.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Vendite di oggi recuperate con successo",
            content = @Content(schema = @Schema(implementation = Sale.class)))
    @GetMapping("/today")
    public ResponseEntity<List<Sale>> getTodaySales() {
        LocalDate today = LocalDate.now();
        List<Sale> todaySales = saleService.getSalesByDate(today);
        return ResponseEntity.ok(todaySales);
    }

    /**
     * Recupera tutte le vendite effettuate nel mese corrente.
     *
     * @return Lista delle vendite del mese corrente.
     */
    @Operation(summary = "Recupera le vendite del mese corrente", description = "Endpoint per ottenere tutte le vendite effettuate nel mese corrente.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Vendite del mese corrente recuperate con successo",
            content = @Content(schema = @Schema(implementation = Sale.class)))
    @GetMapping("/current-month")
    public ResponseEntity<List<Sale>> getCurrentMonthSales() {
        List<Sale> currentMonthSales = saleService.getSalesByCurrentMonth();
        return ResponseEntity.ok(currentMonthSales);
    }

    /**
     * Crea una nuova vendita con più prodotti e quantità.
     *
     * @param sale Dettagli della vendita da creare.
     * @return La vendita creata.
     */
    @Operation(summary = "Crea una nuova vendita", description = "Endpoint per creare una nuova vendita con più prodotti e quantità.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vendita creata con successo",
                    content = @Content(schema = @Schema(implementation = Sale.class))),
            @ApiResponse(responseCode = "400", description = "Errore durante la creazione della vendita",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Sale> createSale(@Valid @RequestBody Sale sale) {
        Sale savedSale = saleService.createSale(sale);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSale);
    }

    /**
     * Aggiorna i dettagli di una vendita esistente.
     *
     * @param id L'ID della vendita da aggiornare.
     * @param saleDetails I nuovi dettagli della vendita.
     * @return La vendita aggiornata.
     */
    @Operation(summary = "Aggiorna una vendita", description = "Endpoint per aggiornare i dettagli di una vendita esistente.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendita aggiornata con successo",
                    content = @Content(schema = @Schema(implementation = Sale.class))),
            @ApiResponse(responseCode = "400", description = "Errore durante l'aggiornamento della vendita",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable Long id, @Valid @RequestBody Sale saleDetails) {
        Sale updatedSale = saleService.updateSale(id, saleDetails);
        return ResponseEntity.ok(updatedSale);
    }

    /**
     * Elimina una vendita e ripristina le quantità dei prodotti venduti.
     *
     * @param id L'ID della vendita da eliminare.
     * @return ResponseEntity senza contenuto se l'operazione ha successo.
     */
    @Operation(summary = "Elimina una vendita", description = "Endpoint per eliminare una vendita e ripristinare le quantità dei prodotti venduti.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vendita eliminata con successo",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Vendita non trovata",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}
