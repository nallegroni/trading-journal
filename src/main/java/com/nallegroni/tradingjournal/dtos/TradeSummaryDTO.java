package com.nallegroni.tradingjournal.dtos;

import java.math.BigDecimal;

import com.nallegroni.tradingjournal.model.Symbol;

import lombok.Data;

@Data
public class TradeSummaryDTO {

    private Long id;
    private Symbol symbol;
    private BigDecimal entryPrice;
    private String tradeStatus;
    private BigDecimal profitLoss;

    public TradeSummaryDTO(Long id, Symbol symbol, BigDecimal entryPrice, String tradeStatus, BigDecimal profitLoss) {
        this.id = id;
        this.symbol = symbol;
        this.entryPrice = entryPrice;
        this.tradeStatus = tradeStatus;
        this.profitLoss = profitLoss;
    }
}
