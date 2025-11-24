package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Transaction {
    private String merchantId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String description;

    public Transaction(String merchantId, BigDecimal amount, String description, Clock clock) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.description = description;
        this.timestamp = clock.now();
    }
}
