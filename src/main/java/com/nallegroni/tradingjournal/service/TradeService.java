package com.nallegroni.tradingjournal.service;

import org.springframework.stereotype.Service;
import com.nallegroni.tradingjournal.dtos.CreateTradeRequestDTO;
import com.nallegroni.tradingjournal.model.Trade;
import com.nallegroni.tradingjournal.model.Trader;
import com.nallegroni.tradingjournal.repository.TradeRepository;
import com.nallegroni.tradingjournal.repository.TraderRepository;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    private final TraderRepository traderRepository;

    public TradeService(TradeRepository tradeRepository, TraderRepository traderRepository) {
        this.tradeRepository = tradeRepository;
        this.traderRepository = traderRepository;
    }

    public Trade createTrade(CreateTradeRequestDTO request) {
        // 1. Buscamos al usuario dueño del trade (Validamos que exista)
        Trader trader = traderRepository.findById(request.getTraderId())
                .orElseThrow(() -> new IllegalArgumentException("El trader con ID " + request.getTraderId() + " no existe."));

        // 2. Convertimos el DTO a la Entidad Trade
        // Usamos el constructor que ya creaste en Trade.java
        Trade newTrade = new Trade(
            request.getSymbol(),
            request.getEntryPrice(),
            request.getTypeOrder(),
            request.getLotSize()
        );

        // 3. Asignamos la relación vital
        newTrade.setTrader(trader);

        // 4. Guardamos en base de datos
        return tradeRepository.save(newTrade);
    }
    
    // Aquí puedes mover también el método de cerrar trade más adelante...
}
