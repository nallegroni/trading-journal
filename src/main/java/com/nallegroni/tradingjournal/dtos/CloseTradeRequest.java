package com.nallegroni.tradingjournal.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CloseTradeRequest {
    private BigDecimal exitPrice;
}