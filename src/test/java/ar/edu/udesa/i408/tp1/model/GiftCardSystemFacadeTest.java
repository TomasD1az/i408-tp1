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
        // El constructor del facade ya precarga usuarios,
        // pero agregamos otro usuario/controlado para test
        facade.issueGiftCard("gc1", "u1", BigDecimal.ZERO);
    }

    @Test
    public void userCanLoadBalanceIntoGiftCard() {
        // usamos issueGiftCard con monto inicial
        facade.issueGiftCard("gcLoad", "u1", new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), facade.getBalance("gcLoad"));
    }

    @Test
    public void userCanSpendFromGiftCardWhenEnoughBalance() {
        facade.issueGiftCard("gcSpend", "u1", new BigDecimal("50.00"));
        // simulamos gasto de merchant
        facade.merchantCharge("merchant-key-abc", "gcSpend", new BigDecimal("20.00"), "test spend");
        assertEquals(new BigDecimal("30.00"), facade.getBalance("gcSpend"));
    }

    @Test
    public void spendingMoreThanBalanceThrowsError() {
        facade.issueGiftCard("gcLow", "u1", new BigDecimal("30.00"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.merchantCharge("merchant-key-abc", "gcLow", new BigDecimal("50.00"), "overspend")
        );

        assertEquals(GiftCardSystemFacade.insufficientBalanceErrorDescription, exception.getMessage());
    }

    @Test
    public void loadingBalanceOnInvalidGiftCardThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.getBalance("invalidCode")
        );

        assertEquals(GiftCardSystemFacade.invalidGiftCardErrorDescription, exception.getMessage());
    }

    @Test
    public void issuingGiftCardForInvalidUserThrowsError() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                facade.issueGiftCard("gcInvalid", "notAUser", BigDecimal.ZERO)
        );

        assertEquals(GiftCardSystemFacade.invalidUserErrorDescription, exception.getMessage());
    }

    @Test
    public void checkBalanceOfNewGiftCardIsZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(facade.getBalance("gc1")));
    }
}