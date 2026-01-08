package com.nallegroni.tradingjournal.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nallegroni.tradingjournal.model.Trade;
import com.nallegroni.tradingjournal.model.Trader;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    long countByTrader_Id(Long traderId);

    List<Trade> findByTrader_Id(Long traderId);

    List<Trade> findByTrader(Trader trader);

    List<Trade> findByTrader_IdOrderByEntryDateDesc(Long traderId, Pageable pageable);
}
