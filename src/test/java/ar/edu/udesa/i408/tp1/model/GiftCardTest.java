package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCardTest {
    private GiftCard card;

    @BeforeEach
    public void setUp() {
        Clock clock = new Clock();
        card = new GiftCard("GC-001", "user1", clock);
    }

    @Test
    public void test01newGiftCardHasZeroBalance() {
        assertEquals(BigDecimal.ZERO, card.getBalance());
        assertEquals("GC-001", card.getCode());
        assertEquals("user1", card.getOwnerId());
    }

    @Test
    public void test02loadIncreasesBalance() {
        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");
        assertEquals(new BigDecimal("100.00"), card.getBalance());
    }

    @Test
    public void test03spendDecreasesBalanceWhenSufficientFunds() {
        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");
        assertTrue(card.spend(new BigDecimal("60.00"), "MERCHANT", "test spend"));
        assertEquals(new BigDecimal("40.00"), card.getBalance());
    }

    @Test
    public void test04canNotSpendMoreThanBalance() {
        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");
        assertFalse(card.spend(new BigDecimal("150.00"), "MERCHANT", "overspend"));
        assertEquals(new BigDecimal("100.00"), card.getBalance());
    }

    @Test
    public void test05transactionsAreRecorded() {
        card.load(new BigDecimal("100.00"), "MERCHANT", "test load");
        card.spend(new BigDecimal("30.00"), "MERCHANT", "test spend");
        assertEquals(2, card.getTransactionHistory().size());
    }
}