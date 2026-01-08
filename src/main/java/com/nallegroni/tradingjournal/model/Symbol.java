package com.nallegroni.tradingjournal.model;

import java.math.BigDecimal;

import com.nallegroni.tradingjournal.model.enums.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "symbols")
public class Symbol {
    
    @Id
    @Column(length = 10)
    private String symbolCode;

    @Enumerated(EnumType.STRING)
    private Currency baseCurrency;
    @Enumerated(EnumType.STRING)
    private Currency quoteCurrency;

    private BigDecimal contractSize;

    public Symbol(Currency baseCurrency, Currency quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.symbolCode = baseCurrency.name() + quoteCurrency.name();
        this.contractSize = determineDefaultContractSize(baseCurrency);
    }

    private BigDecimal determineDefaultContractSize(Currency base) {
        return switch (base) {
            // Regla 1: Metales Preciosos
            case XAU -> new BigDecimal("100");    // 1 Lote de Oro = 100 Onzas
            case XAG -> new BigDecimal("5000");   // 1 Lote de Plata = 5000 Onzas
            
            // Regla 2: Criptomonedas (Generalmente el contrato es 1 moneda)
            case BTC, ETH -> BigDecimal.ONE; // 1 Lote = 1 Bitcoin
            
            // Regla 3: Forex (Divisas FIAT estándar)
            // Cualquier otra moneda (EUR, USD, GBP, JPY, etc.)
            default -> new BigDecimal("100000"); // 1 Lote Estándar = 100,000 unidades
        };
    }
}
