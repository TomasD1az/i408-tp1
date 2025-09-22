package ar.edu.udesa.i408.tp1.model;


import lombok.Getter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class GiftCard {
    private String code;
    private String ownerId;
    private BigDecimal balance;
    private List<Transaction> transactions = new ArrayList<>();
    private Clock clock;

    public GiftCard(String code, String ownerId, Clock clock) {
        this.code = code;
        this.ownerId = ownerId;
        this.balance = BigDecimal.ZERO;
        this.clock = clock;
    }

    public void load(BigDecimal amount, String merchantId, String description) {
        assertValidAmount(amount);

        balance = balance.add(amount);
        transactions.add(new Transaction(merchantId, amount, description, clock));
    }

    public boolean spend(BigDecimal amount, String merchantId, String description) {
        assertValidAmount(amount);

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

    private void assertValidAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("amount must be positive");
    }
}