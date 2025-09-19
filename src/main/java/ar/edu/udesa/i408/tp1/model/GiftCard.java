package ar.edu.udesa.i408.tp1.model;


import lombok.Getter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class GiftCard {
    private final String code;
    private final String ownerId;
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();
    private final Clock clock;

    public GiftCard(String code, String ownerId, Clock clock) {
        this.code = code;
        this.ownerId = ownerId;
        this.balance = BigDecimal.ZERO;
        this.clock = clock;
    }

    public void load(BigDecimal amount, String merchantId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be positive");

        balance = balance.add(amount);
        transactions.add(new Transaction(merchantId, amount, description, clock));
    }

    public boolean spend(BigDecimal amount, String merchantId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be positive");

        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
            transactions.add(new Transaction(merchantId, amount.negate(), description, clock));
            return true;
        }
        return false;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }
}
