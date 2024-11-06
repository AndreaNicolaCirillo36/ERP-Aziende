package com.azienda.erp.erp_backend.repository;

import com.azienda.erp.erp_backend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}