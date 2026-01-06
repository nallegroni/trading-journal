package com.nallegroni.tradingjournal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nallegroni.tradingjournal.dtos.CloseTradeRequest;
import com.nallegroni.tradingjournal.model.Trade;
import com.nallegroni.tradingjournal.repository.TradeRepository;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeRepository tradeRepository;

    public TradeController(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @PostMapping
    public Trade createTrade(@RequestBody Trade trade) {
        return tradeRepository.save(trade);
    }
    
    @GetMapping
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    @PatchMapping("/{id}/close")
    public Trade closeTrade(@PathVariable Long id, @RequestBody CloseTradeRequest closeRequest) {
        Trade existingTrade = tradeRepository.findById(id).orElseThrow();

        existingTrade.closeOperation(closeRequest.getExitPrice());
        
        return tradeRepository.save(existingTrade);
    }

}