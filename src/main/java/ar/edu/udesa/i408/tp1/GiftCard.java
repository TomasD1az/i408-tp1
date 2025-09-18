package ar.edu.udesa.i408.tp1.model;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GiftCard {
    private final String code;
    private final String ownerId;
    private BigDecimal balance;

    public GiftCard(String code, String ownerId) {
        this.code = code;
        this.ownerId = ownerId;
        this.balance = BigDecimal.ZERO;
    }

    public void load(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public boolean spend(BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
            return true;
        }
        return false;
    }
}


