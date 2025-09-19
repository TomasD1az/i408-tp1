package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCardTest {

    @Test
    public void newGiftCardHasZeroBalance() {
        Clock clock = new Clock();
        GiftCard card = new GiftCard("GC-001", "user1", clock);

        assertEquals(BigDecimal.ZERO, card.getBalance());
        assertEquals("GC-001", card.getCode());
        assertEquals("user1", card.getOwnerId());
    }

    @Test
    public void loadIncreasesBalance() {
        Clock clock = new Clock();
        GiftCard card = new GiftCard("GC-001", "user1", clock);

        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");

        assertEquals(new BigDecimal("100.00"), card.getBalance());
    }

    @Test
    public void spendDecreasesBalanceWhenSufficientFunds() {
        Clock clock = new Clock();
        GiftCard card = new GiftCard("GC-001", "user1", clock);
        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");

        boolean result = card.spend(new BigDecimal("60.00"), "MERCHANT", "test spend");

        assertTrue(result);
        assertEquals(new BigDecimal("40.00"), card.getBalance());
    }

    @Test
    public void canNotSpendMoreThanBalance() {
        Clock clock = new Clock();
        GiftCard card = new GiftCard("GC-001", "user1", clock);
        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");

        boolean result = card.spend(new BigDecimal("150.00"), "MERCHANT", "overspend");

        assertFalse(result);
        assertEquals(new BigDecimal("100.00"), card.getBalance());
    }

    @Test
    public void transactionsAreRecorded() {
        Clock clock = new Clock();
        GiftCard card = new GiftCard("GC-001", "user1", clock);

        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");
        card.spend(new BigDecimal("30.00"), "MERCHANT", "test spend");

        assertEquals(2, card.getTransactionHistory().size());
    }
}