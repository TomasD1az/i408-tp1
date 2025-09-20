package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Transaction {
    public static final String CANNOT_CHARGE_A_NEGATIVE_AMOUNT = "Cannot charge a negative amount";
    public static final String CANNOT_CREATE_A_TRANSACTION_WITH_INVALID_TIMESTAMP = "Cannot create a transaction with invalid timestamp";

    private final String merchantId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(String merchantId, BigDecimal amount, String description, Clock clock) {
        assertValidMerchant(merchantId);
        assertValidAmount(amount);
        assertValidTime(clock);

        this.merchantId = merchantId;
        this.amount = amount;
        this.description = description;
        this.timestamp = clock.now();
    }

    private void assertValidTime(Clock clock) {
        if (timestamp == null || timestamp.isAfter(clock.now())) throw new IllegalArgumentException(CANNOT_CREATE_A_TRANSACTION_WITH_INVALID_TIMESTAMP);
    }

    private void assertValidAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException(CANNOT_CHARGE_A_NEGATIVE_AMOUNT);
    }

}
