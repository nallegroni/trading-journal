package com.nallegroni.tradingjournal.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CloseTradeRequestDTO {
    private BigDecimal exitPrice;
}