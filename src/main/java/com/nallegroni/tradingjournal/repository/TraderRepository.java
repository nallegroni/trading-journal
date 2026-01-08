package com.nallegroni.tradingjournal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nallegroni.tradingjournal.model.Trader;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

}
