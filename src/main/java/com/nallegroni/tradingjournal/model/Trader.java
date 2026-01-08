package com.nallegroni.tradingjournal.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.nallegroni.tradingjournal.model.enums.Currency;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "traders")
public class Trader {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal balance;
    private long totalCount;
    private List<Trade> trades;
    private BigDecimal profitLoss;

    @Enumerated(EnumType.STRING)
    private Currency accountCurrency;


    public Trader(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
        this.accountCurrency = Currency.USD;
        this.totalCount = 0;
        this.trades = new ArrayList<>();
        this.profitLoss = BigDecimal.ZERO;
    }

    public Trader(String name, BigDecimal balance, Currency accountCurrency) {
        this.name = name;
        this.balance = balance;
        this.accountCurrency = accountCurrency;
        this.totalCount = 0;
        this.trades = new ArrayList<>();
        this.profitLoss = BigDecimal.ZERO;
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void deductFromBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void addTrade(Trade trade) {
        this.trades.add(trade);
        this.totalCount++;
        this.profitLoss = this.profitLoss.add(trade.getProfitLoss());
    }


}
