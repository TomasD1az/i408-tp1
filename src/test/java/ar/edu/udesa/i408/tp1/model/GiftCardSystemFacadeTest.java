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
        facade.registerUser("u1", "Alice", "alice@mail.com");
        facade.issueGiftCard("gc1", "u1");
    }

    @Test
    public void userCanLoadBalanceIntoGiftCard() {
        facade.loadGiftCard("gc1", new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), facade.checkBalance("gc1"));
    }

    @Test
    public void userCanSpendFromGiftCardWhenEnoughBalance() {
        facade.loadGiftCard("gc1", new BigDecimal("50.00"));
        facade.spendFromGiftCard("gc1", new BigDecimal("20.00"));
        assertEquals(new BigDecimal("30.00"), facade.checkBalance("gc1"));
    }

    @Test
    public void spendingMoreThanBalanceThrowsError() {
        facade.loadGiftCard("gc1", new BigDecimal("30.00"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.spendFromGiftCard("gc1", new BigDecimal("50.00"))
        );

        assertEquals(GiftCardSystemFacade.insufficientBalanceErrorDescription, exception.getMessage());
    }

    @Test
    public void loadingBalanceOnInvalidGiftCardThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.loadGiftCard("invalidCode", new BigDecimal("10.00"))
        );

        assertEquals(GiftCardSystemFacade.invalidGiftCardErrorDescription, exception.getMessage());
    }

    @Test
    public void issuingGiftCardForInvalidUserThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.issueGiftCard("gcInvalid", "notAUser")
        );

        assertEquals(GiftCardSystemFacade.invalidUserErrorDescription, exception.getMessage());
    }

    @Test
    public void checkBalanceOfNewGiftCardIsZero() {
        assertEquals(0, new BigDecimal("100.00").compareTo(facade.checkBalance("gc1")));
    }
}