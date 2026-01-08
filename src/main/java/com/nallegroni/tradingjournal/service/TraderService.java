package com.nallegroni.tradingjournal.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.nallegroni.tradingjournal.dtos.TradeSummaryDTO;
import com.nallegroni.tradingjournal.dtos.TraderProfileDTO;
import com.nallegroni.tradingjournal.model.Trade;
import com.nallegroni.tradingjournal.model.Trader;
import com.nallegroni.tradingjournal.repository.TradeRepository;
import com.nallegroni.tradingjournal.repository.TraderRepository;

@Service
public class TraderService {

    private final TraderRepository traderRepository;
    private final TradeRepository tradeRepository;

    public TraderService(TraderRepository traderRepository, TradeRepository tradeRepository) {
        this.traderRepository = traderRepository;
        this.tradeRepository = tradeRepository;
    }

    // Método que pide el Controller
    public TraderProfileDTO getTraderProfile(Long traderId) {
        // 1. Buscamos al usuario (usando traderRepository)
        Trader trader = traderRepository.findById(traderId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        long totalCount = tradeRepository.countByTrader_Id(traderId);

        // 2. Obtenemos SOLO los últimos 5 trades (rápido y liviano)
        PageRequest limitFive = PageRequest.of(0, 5); // Página 0, tamaño 5
        List<Trade> lastTrades = tradeRepository.findByTrader_IdOrderByEntryDateDesc(traderId, limitFive);
        
        // 3. Convertimos Trades a DTOs (Mapeo)
        List<TradeSummaryDTO> lastTradesDTOs = lastTrades.stream()
            .map(t -> new TradeSummaryDTO(t.getId(), 
                                          t.getSymbol(), 
                                          t.getEntryPrice(), 
                                          t.getTradeStatus().name(),
                                          t.getProfitLoss()) // Convertimos el Enum a String
            ).toList();

        // 4. Retornamos un objeto combinado (DTO)
        return new TraderProfileDTO(trader.getName(), trader.getBalance(), totalCount, lastTradesDTOs);
    }

    public Trader createTraderProfile(TraderProfileDTO traderProfileDTO) {
        Trader trader = new Trader(traderProfileDTO.getName(), traderProfileDTO.getBalance());
        
        return traderRepository.save(trader);

        
    }
}
