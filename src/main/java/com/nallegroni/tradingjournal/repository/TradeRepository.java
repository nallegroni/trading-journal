package com.nallegroni.tradingjournal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nallegroni.tradingjournal.model.Trade;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

}
