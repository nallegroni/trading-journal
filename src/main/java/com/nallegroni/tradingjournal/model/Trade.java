package com.nallegroni.tradingjournal.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.nallegroni.tradingjournal.model.enums.Currency;
import com.nallegroni.tradingjournal.model.enums.TradeStatus;
import com.nallegroni.tradingjournal.model.enums.TypeOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "trades")
public class Trade {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trader trader;

    @ManyToOne(fetch = FetchType.EAGER)
    private Symbol symbol;
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

    private BigDecimal profitLoss = BigDecimal.ZERO;


    public Trade(Symbol symbol, BigDecimal entryPrice, TypeOrder typeOrder, BigDecimal lotSize) {
        this.symbol = symbol;
        this.typeOrder = typeOrder;
        this.lotSize = lotSize;
        this.entryPrice = entryPrice;
        this.entryDate = LocalDateTime.now();
        this.tradeStatus = TradeStatus.OPEN;
    }

    public Trade(Symbol symbol, BigDecimal entryPrice, TypeOrder typeOrder, BigDecimal lotSize, BigDecimal SL, BigDecimal TP) {
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

        this.profitLoss = this.calculateProfitLoss();

        // signum devuelve 1 si es positivo, -1 si es negativo, 0 si es cero
        int result = this.profitLoss.signum();

        if (result > 0) {
            this.tradeStatus = TradeStatus.WIN;
        } else if (result < 0) {
            this.tradeStatus = TradeStatus.LOSS;
        } else {
            this.tradeStatus = TradeStatus.BREAK_EVEN;
        }
    }

    protected BigDecimal calculateProfitLoss() {
        // P/L = (Precio de Salida - Precio de Entrada) * Volumen en Unidades
        BigDecimal contractSize = this.symbol.getContractSize();
        BigDecimal unitsVolume = this.lotSize.multiply(contractSize);

        BigDecimal priceDifference;
        if (this.typeOrder == TypeOrder.BUY) {
            priceDifference = this.exitPrice.subtract(this.entryPrice);
        } else {
            priceDifference = this.entryPrice.subtract(this.exitPrice);
        }

        BigDecimal rawPnL = priceDifference.multiply(unitsVolume);
        Currency accountCurrency = this.getTrader().getAccountCurrency();
        Currency baseCurrency = this.getSymbol().getBaseCurrency();   // Ej: "EUR" en EURUSD
        Currency quoteCurrency = this.getSymbol().getQuoteCurrency(); // Ej: "USD" en EURUSD

        BigDecimal pnl;
        if (quoteCurrency.equals(accountCurrency)) {
            // CASO 1: Par Directo (Ej: EUR/USD, GBP/USD, XAU/USD)
            // El resultado ya está en la moneda de la cuenta.
            pnl = rawPnL;

        } else if (baseCurrency.equals(accountCurrency)) {
            // CASO 2: Par Inverso (Ej: USD/JPY, USD/CHF, USD/CAD)
            // El resultado está en JPY, CHF, etc.
            // Para pasar a USD, DIVIDIMOS por el precio de salida (Current Rate).
            // Fórmula: PnL_USD = PnL_JPY / Precio_USDJPY
            
            // Nota: MathContext.DECIMAL128 para precisión en división
            pnl = rawPnL.divide(this.exitPrice, MathContext.DECIMAL128);

        } else {
            // CASO 3: Par Cruzado (Ej: EUR/GBP con cuenta en USD)
            // El resultado está en GBP. Necesitamos la tasa GBP/USD para convertir.
            // Esto es más complejo porque necesitas buscar el precio de OTRO símbolo en tu API.
            
            // BigDecimal conversionRate = tradingService.getPrice(quoteCurrency + accountCurrency);
            // this.profitLoss = rawPnL.multiply(conversionRate);
            
            // Por ahora, si no tienes ese servicio a mano, puedes dejarlo pendiente o lanzar una excepción.
            System.out.println("Conversión de par cruzado requerida");
            pnl = rawPnL; // Valor en moneda cotizada (incorrecto para el balance)
        }

        // Redondeo final a 2 decimales para mostrar en el balance
        return pnl.setScale(2, RoundingMode.HALF_UP);
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
