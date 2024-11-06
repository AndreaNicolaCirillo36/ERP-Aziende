package com.azienda.erp.erp_backend.repository;

import com.azienda.erp.erp_backend.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Recupera tutte le vendite ordinate per data di vendita in ordine discendente.
     *
     * @return un elenco di tutte le vendite ordinate per data di vendita in ordine discendente.
     */
    List<Sale> findAllByOrderBySaleDateDesc();

    /**
     * Recupera le prime 12 vendite ordinate per data di vendita in ordine discendente.
     *
     * @return un elenco delle prime 12 vendite ordinate per data di vendita in ordine discendente.
     */
    List<Sale> findTop12ByOrderBySaleDateDesc();

    /**
     * Recupera tutte le vendite tra l'inizio e la fine della giornata specificata.
     *
     * @param startOfDay l'inizio della giornata per filtrare le vendite.
     * @param endOfDay la fine della giornata per filtrare le vendite.
     * @return un elenco di tutte le vendite con una data di vendita compresa tra l'inizio e la fine della giornata specificata.
     */
    List<Sale> findBySaleDateBetween(ZonedDateTime startOfDay, ZonedDateTime endOfDay);
}