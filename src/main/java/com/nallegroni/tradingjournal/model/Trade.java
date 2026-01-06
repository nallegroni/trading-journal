package com.nallegroni.tradingjournal.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nallegroni.tradingjournal.model.enums.TradeStatus;
import com.nallegroni.tradingjournal.model.enums.TypeOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Trade {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private BigDecimal lotSize;

    private BigDecimal entryPrice;
    private LocalDateTime entryDate;
    private BigDecimal exitPrice = null;
    private LocalDateTime exitDate = null;

    private BigDecimal stopLoss = null;
    private BigDecimal takeProfit = null;

    @Enumerated(EnumType.STRING)
    private TypeOrder typeOrder;

    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;

    public Trade(String symbol, BigDecimal entryPrice, TypeOrder typeOrder, BigDecimal lotSize) {
        this.symbol = symbol;
        this.typeOrder = typeOrder;
        this.lotSize = lotSize;
        this.entryPrice = entryPrice;
        this.entryDate = LocalDateTime.now();
        this.tradeStatus = TradeStatus.OPEN;
    }

    public Trade(String symbol, BigDecimal entryPrice, TypeOrder typeOrder, BigDecimal lotSize, BigDecimal SL, BigDecimal TP) {
        this.symbol = symbol;
        this.typeOrder = typeOrder;
        this.lotSize = lotSize;
        this.entryPrice = entryPrice;
        this.entryDate = LocalDateTime.now();
        this.tradeStatus = TradeStatus.OPEN;

        setStopLoss(SL);
        setTakeProfit(TP);
    }

    private void setStopLoss(BigDecimal stopLoss) {
        this.validateSL(stopLoss);
        this.stopLoss = stopLoss;
    }

    private void setTakeProfit(BigDecimal takeProfit) {
        this.validateTP(takeProfit);
        this.takeProfit = takeProfit;
    }
        
    public void closeOperation(BigDecimal closingPrice) {
        BigDecimal exitPrice = closingPrice;
        LocalDateTime exitDate = LocalDateTime.now();
        
        this.validateExitPrice(exitPrice);
        this.exitPrice = exitPrice;

        this.validateExitDate(exitDate);
        this.exitDate = exitDate;

        BigDecimal profitOrLoss;
        
        if (this.typeOrder == TypeOrder.BUY) {
            profitOrLoss = this.exitPrice.subtract(this.entryPrice);
        } else {
            profitOrLoss = this.entryPrice.subtract(this.exitPrice);
        }

        // signum devuelve 1 si es positivo, -1 si es negativo, 0 si es cero
        int result = profitOrLoss.signum();

        if (result > 0) {
            this.tradeStatus = TradeStatus.WIN;
        } else if (result < 0) {
            this.tradeStatus = TradeStatus.LOSS;
        } else {
            this.tradeStatus = TradeStatus.BREAK_EVEN;
        }
    }

    // Validaciones

    protected void validateExitPrice(BigDecimal exitPrice) {
        if (exitPrice.signum() < 0) {
            throw new IllegalStateException("Error: exitPrice can't be a negative value. Check the entered value.");
        }
    }

    protected void validateExitDate(LocalDateTime exitDate) {
        if (exitDate.isBefore(this.entryDate)) {
            throw new IllegalStateException("Error: You can't close a trade before its opening date. Check the entered date.");
        }
    }

    protected void validateSL(BigDecimal SL) {
        BigDecimal difference;
        
        if (this.typeOrder == TypeOrder.BUY) {
            difference = this.getEntryPrice().subtract(SL);
        } else {
            difference = SL.subtract(this.getEntryPrice());
        }

        if (difference.signum() <= 0) {
            throw new IllegalStateException("Error: Stop Loss must be less than Entry Price for BUY orders and greater than Entry Price for SELL orders. Check the entered value.");
        }
    }

    protected void validateTP(BigDecimal TP) {
        BigDecimal difference;
        
        if (this.typeOrder == TypeOrder.BUY) {
            difference = TP.subtract(this.getEntryPrice());
        } else {
            difference = this.getEntryPrice().subtract(TP);
        }
        
        if (difference.signum() <= 0) {
            throw new IllegalStateException("Error: Take Profit must be greater than Entry Price for BUY orders and less than Entry Price for SELL orders. Check the entered value.");
        }
    }

}
