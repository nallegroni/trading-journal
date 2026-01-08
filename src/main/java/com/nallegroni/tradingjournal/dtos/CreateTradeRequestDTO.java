package com.nallegroni.tradingjournal.dtos;

import java.math.BigDecimal;

import com.nallegroni.tradingjournal.model.Symbol;
import com.nallegroni.tradingjournal.model.enums.TypeOrder;

import lombok.Data;

@Data
public class CreateTradeRequestDTO {
    
    private Symbol symbol;
    private BigDecimal entryPrice;
    private TypeOrder typeOrder;
    private BigDecimal lotSize;
    private long traderId;
}
