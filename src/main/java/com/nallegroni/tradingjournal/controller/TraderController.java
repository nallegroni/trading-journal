package com.nallegroni.tradingjournal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.nallegroni.tradingjournal.dtos.TraderProfileDTO;
import com.nallegroni.tradingjournal.model.Trader;
import com.nallegroni.tradingjournal.service.TraderService;

@RestController
@RequestMapping("/api/traders")
public class TraderController {

    private final TraderService traderService;

    public TraderController(TraderService traderService) {
        this.traderService = traderService;
    }

    @PostMapping
    public Trader createTraderProfile(@RequestBody TraderProfileDTO traderProfileDTO) {
        return traderService.createTraderProfile(traderProfileDTO);
    }

    @GetMapping("/{id}/profile")
    public TraderProfileDTO getTraderProfile(@PathVariable Long id) {
        return traderService.getTraderProfile(id);
    }



}
