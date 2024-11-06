package com.azienda.erp.erp_backend.controller;

import com.azienda.erp.erp_backend.entity.Product;
import com.azienda.erp.erp_backend.service.ProductService;
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
 * Controller per la gestione dei prodotti nell'applicazione ERP.
 * Espone vari endpoint per creare, recuperare, aggiornare e cancellare prodotti.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Recupera tutti i prodotti presenti nel sistema.
     *
     * @return Lista di tutti i prodotti.
     */
    @Operation(summary = "Recupera tutti i prodotti", description = "Endpoint per ottenere la lista di tutti i prodotti presenti nel sistema.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Lista dei prodotti recuperata con successo",
            content = @Content(schema = @Schema(implementation = Product.class)))
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProduct();
        return ResponseEntity.ok(products);
    }

    /**
     * Recupera un prodotto a partire dal suo barcode.
     *
     * @param barcode Il barcode del prodotto da recuperare.
     * @return Il prodotto corrispondente al barcode fornito.
     */
    @Operation(summary = "Recupera un prodotto tramite barcode", description = "Endpoint per ottenere un prodotto a partire dal suo barcode.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prodotto trovato",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Prodotto non trovato",
                    content = @Content)
    })
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Product> getProductByBarcode(@PathVariable Long barcode) {
        Product product = productService.findByBarcode(barcode);
        return ResponseEntity.ok(product);
    }

    /**
     * Crea un nuovo prodotto e lo associa a un fornitore.
     *
     * @param product Dettagli del prodotto da creare.
     * @return Il nuovo prodotto creato.
     */
    @Operation(summary = "Crea un nuovo prodotto", description = "Endpoint per creare un nuovo prodotto e associarlo a un fornitore.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prodotto creato con successo",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Errore: Il barcode deve essere unico",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Errore imprevisto durante la creazione del prodotto",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    /**
     * Recupera un prodotto a partire dal suo ID.
     *
     * @param id L'ID del prodotto da recuperare.
     * @return Il prodotto corrispondente all'ID fornito.
     */
    @Operation(summary = "Recupera un prodotto per ID", description = "Endpoint per ottenere un prodotto a partire dal suo ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prodotto trovato",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Prodotto non trovato",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Aggiorna le informazioni di un prodotto esistente.
     *
     * @param id L'ID del prodotto da aggiornare.
     * @param productDetails I nuovi dettagli del prodotto.
     * @return Il prodotto aggiornato.
     */
    @Operation(summary = "Aggiorna un prodotto esistente", description = "Endpoint per aggiornare le informazioni di un prodotto esistente.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prodotto aggiornato con successo",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Prodotto o fornitore non trovato",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Cancella un prodotto a partire dal suo ID.
     *
     * @param id L'ID del prodotto da cancellare.
     * @return ResponseEntity senza contenuto se l'operazione ha successo.
     */
    @Operation(summary = "Cancella un prodotto", description = "Endpoint per cancellare un prodotto a partire dal suo ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prodotto cancellato con successo",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Prodotto non trovato",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
