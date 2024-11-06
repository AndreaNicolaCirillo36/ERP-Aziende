package com.azienda.erp.erp_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Data
@ToString(exclude = "saleItems")
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotNull
    private List<SaleItem> saleItems;

    @Column(nullable = false)
    private ZonedDateTime saleDate;

    @Column(nullable = false)
    @NotNull
    private double totalPrice;

    @Column(nullable = false)
    @NotNull
    private double netProfit;

    @Column(nullable = true)
    private double discount;

    @Column(nullable = false)
    @NotNull
    private String paymentMethods;

    @Column(nullable = true)
    private String note;

    @Column(nullable = true)
    private Long totalProducts;

    @PrePersist
    public void prePersist() {
        this.saleDate = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
    }
}
