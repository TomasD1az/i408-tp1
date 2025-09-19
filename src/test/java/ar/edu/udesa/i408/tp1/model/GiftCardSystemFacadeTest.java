package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCardSystemFacadeTest {

    private GiftCardSystemFacade facade;

    @BeforeEach
    public void setUp() {
        facade = new GiftCardSystemFacade();
    }

    @Test
    public void preloadedGiftCardsAreAvailable() {
        // u1 should have GC-1001 preloaded with 1000
        assertEquals(new BigDecimal("1000.00"), facade.getBalance("GC-1001"));

        // u2 should have GC-2001 preloaded with 500
        assertEquals(new BigDecimal("500.00"), facade.getBalance("GC-2001"));

        // u3 should have GC-3001 preloaded with 3000
        assertEquals(new BigDecimal("3000.00"), facade.getBalance("GC-3001"));

        // u4 should have GC-4001 preloaded with 4000
        assertEquals(new BigDecimal("4000.00"), facade.getBalance("GC-4001"));
    }

    @Test
    public void userCanSpendFromGiftCardWhenEnoughBalance() {
        facade.merchantCharge("merchant-key-abc", "GC-1001", new BigDecimal("200.00"), "test spend");
        assertEquals(new BigDecimal("800.00"), facade.getBalance("GC-1001"));
    }

    @Test
    public void spendingMoreThanBalanceThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.merchantCharge("merchant-key-abc", "GC-2001", new BigDecimal("1000.00"), "overspend")
        );
        assertEquals(GiftCardSystemFacade.insufficientBalanceErrorDescription, exception.getMessage());
    }

    @Test
    public void queryingInvalidGiftCardThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.getBalance("invalidCode")
        );
        assertEquals(GiftCardSystemFacade.invalidGiftCardErrorDescription, exception.getMessage());
    }

    @Test
    public void merchantChargeWithInvalidKeyThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.merchantCharge("bad-merchant", "GC-1001", new BigDecimal("50.00"), "test")
        );
        assertEquals(GiftCardSystemFacade.invalidMerchantErrorDescription, exception.getMessage());
    }

    @Test
    public void transactionHistoryIsUpdatedOnSpend() {
        int before = facade.getTransactions("GC-1001").size();
        facade.merchantCharge("merchant-key-abc", "GC-1001", new BigDecimal("100.00"), "spend test");
        int after = facade.getTransactions("GC-1001").size();

        assertEquals(before + 1, after);
    }
}
