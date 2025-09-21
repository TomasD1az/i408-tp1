package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Transaction {
    private final String merchantId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(String merchantId, BigDecimal amount, String description, Clock clock) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.description = description;
        this.timestamp = clock.now();
    }
}
