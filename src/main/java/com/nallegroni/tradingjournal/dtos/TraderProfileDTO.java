package com.nallegroni.tradingjournal.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TraderProfileDTO {
    
    private String name;
    private BigDecimal balance;
    private long totalCount;
    private List<TradeSummaryDTO> recentTrades;

    public TraderProfileDTO(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
        this.totalCount = 0;
        this.recentTrades = new ArrayList<>();
    }

    public TraderProfileDTO(String name, BigDecimal balance, long totalCount, List<TradeSummaryDTO> recentTrades) {
        this.name = name;
        this.balance = balance;
        this.totalCount = totalCount;
        this.recentTrades = recentTrades;
    }
}
