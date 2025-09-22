package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCardSystemFacadeTest {

    GiftCardSystemFacade systemFacade;

    @BeforeEach
    public void beforeEach() {
        systemFacade = systemFacade();
    }

    @Test
    public void test01CanLoginWithValidCredentials() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void test02CanNotLoginWithInvalidUser() {
        assertThrowsLike(() -> systemFacade().login("invalidUser", "password123"),
                Authentication.userNotFoundErrorDescription);
    }

    @Test
    public void test03CanNotLoginWithInvalidPassword() {
        assertThrowsLike(() -> systemFacade().login("u1", "wrongPassword"),
                Authentication.passwordsDoNotMatchErrorDescription);
    }

    @Test
    public void test04CanNotLoginWithNullCredentials() {
        assertThrowsLike(() -> systemFacade().login(null, "password"),
                Authentication.invalidInputErrorDescription);
    }

    @Test
    public void test05CanNotLoginWithEmptyCredentials() {
        assertThrowsLike(() -> systemFacade().login("u1", ""),
                Authentication.invalidInputErrorDescription);
    }

    @Test
    public void test06CanClaimGiftCardsWithValidToken() {
        String token = systemFacade.login("u1", "Pinamar123");
        List<GiftCard> claimedCards = systemFacade.claimGiftCards(token);

        assertEquals(1, claimedCards.size());
        assertEquals("GC-1001", claimedCards.get(0).getCode());
        assertEquals("u1", claimedCards.get(0).getOwnerId());
    }

    @Test
    public void test07CanNotClaimGiftCardsWithInvalidToken() {
        assertThrowsLike(() -> systemFacade.claimGiftCards("invalidToken"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test08CanCheckBalanceOfOwnedGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");

        assertEquals(new BigDecimal("1000.00"), balance);
    }

    @Test
    public void test09CanNotCheckBalanceOfNotOwnedGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getBalance(token, "GC-2001"), // This belongs to u2
                GiftCardSystemFacade.giftCardNotOwnedByUserErrorDescription);
    }

    @Test
    public void test10CanNotCheckBalanceWithInvalidToken() {
        assertThrowsLike(() -> systemFacade.getBalance("invalidToken", "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test11CanNotCheckBalanceOfInvalidGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getBalance(token, "INVALID-CODE"),
                GiftCardSystemFacade.invalidGiftCardErrorDescription);
    }

    @Test
    public void test12MerchantCanChargeGiftCard() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal chargeAmount = new BigDecimal("200.00");
        BigDecimal expectedBalance = new BigDecimal("800.00");

        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", chargeAmount, "test purchase");

        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");
        assertEquals(expectedBalance, balance);
    }

    @Test
    public void test13CanNotChargeMoreThanGiftCardBalance() {
        assertThrowsLike(() -> systemFacade.merchantCharge("merchant-key-abc", "GC-2001",
                        new BigDecimal("1500.00"), "overspend"),
                GiftCardSystemFacade.insufficientBalanceErrorDescription);
    }

    @Test
    public void test14CanNotChargeWithInvalidMerchantKey() {
        assertThrowsLike(() -> systemFacade.merchantCharge("invalid-key", "GC-1001",
                        new BigDecimal("100.00"), "test"),
                GiftCardSystemFacade.invalidMerchantErrorDescription);
    }

    @Test
    public void test15CanNotChargeInvalidGiftCard() {
        assertThrowsLike(() -> systemFacade.merchantCharge("merchant-key-abc", "INVALID-CODE",
                        new BigDecimal("100.00"), "test"),
                GiftCardSystemFacade.invalidGiftCardErrorDescription);
    }

    @Test
    public void test16TransactionHistoryIsUpdatedOnCharge() {
        String token = systemFacade.login("u1", "Pinamar123");
        int initialTransactions = systemFacade.getTransactionHistory(token, "GC-1001").size();

        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", new BigDecimal("100.00"), "test purchase");

        List<Transaction> transactions = systemFacade.getTransactionHistory(token, "GC-1001");
        assertEquals(initialTransactions + 1, transactions.size());

        Transaction lastTransaction = transactions.get(transactions.size() - 1);
        // Fixed: Use absolute value since spend transactions are stored as negative
        assertEquals(new BigDecimal("100.00"), lastTransaction.getAmount().abs());
        assertEquals("test purchase", lastTransaction.getDescription());
    }

    @Test
    public void test17CanNotGetTransactionHistoryOfNotOwnedGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getTransactionHistory(token, "GC-2001"), // This belongs to u2
                GiftCardSystemFacade.giftCardNotOwnedByUserErrorDescription);
    }

    @Test
    public void test18CanNotGetTransactionHistoryWithInvalidToken() {
        assertThrowsLike(() -> systemFacade.getTransactionHistory("invalidToken", "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }


    @Test
    public void test19CanNotUseOperationsWithExpiredToken() {
        GiftCardSystemFacade systemFacade = systemFacade(expiredTokenClock());

        String token = systemFacade.login("u1", "Pinamar123");
        assertThrowsLike(() -> systemFacade.claimGiftCards(token),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test20CanNotCheckBalanceWithExpiredToken() {
        GiftCardSystemFacade systemFacade = systemFacade(expiredTokenClock());

        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getBalance(token, "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test21CanNotGetTransactionHistoryWithExpiredToken() {
        GiftCardSystemFacade systemFacade = systemFacade(expiredTokenClock());

        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getTransactionHistory(token, "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test22LogoutRemovesTokenFromSystem() {
        String token = systemFacade.login("u1", "Pinamar123");

        // Verify token works
        List<GiftCard> cards = systemFacade.claimGiftCards(token);
        assertEquals(1, cards.size());

        // Logout
        systemFacade.logout(token);

        // Verify token no longer works
        assertThrowsLike(() -> systemFacade.claimGiftCards(token),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test23MultipleUsersCanHaveValidTokensSimultaneously() {
        String tokenU1 = systemFacade.login("u1", "Pinamar123");
        String tokenU2 = systemFacade.login("u2", "CampoGrande2025");

        List<GiftCard> cardsU1 = systemFacade.claimGiftCards(tokenU1);
        List<GiftCard> cardsU2 = systemFacade.claimGiftCards(tokenU2);

        assertEquals(1, cardsU1.size());
        assertEquals("GC-1001", cardsU1.get(0).getCode());
        assertEquals(1, cardsU2.size());
        assertEquals("GC-2001", cardsU2.get(0).getCode());
    }

    @Test
    public void test24MultipleChargesReduceBalanceCorrectly() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal firstCharge = new BigDecimal("300.00");
        BigDecimal secondCharge = new BigDecimal("200.00");
        BigDecimal expectedBalance = new BigDecimal("500.00");

        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", firstCharge, "first purchase");
        systemFacade.merchantCharge("merchant-key-xyz", "GC-1001", secondCharge, "second purchase");

        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");
        assertEquals(expectedBalance, balance);
    }

    @Test
    public void test25DifferentMerchantsCanChargeGiftCard() {
        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", new BigDecimal("100.00"), "merchant abc");
        systemFacade.merchantCharge("merchant-key-xyz", "GC-1001", new BigDecimal("150.00"), "merchant xyz");
        systemFacade.merchantCharge("merchant-key-123", "GC-1001", new BigDecimal("50.00"), "merchant 123");

        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");
        assertEquals(new BigDecimal("700.00"), balance);

        List<Transaction> transactions = systemFacade.getTransactionHistory(token, "GC-1001");
        assertTrue(transactions.size() >= 3); // At least the 3 new transactions plus initial load
    }

    
    private static Clock expiredTokenClock() {
        return new Clock() {
            private final LocalDateTime baseTime = LocalDateTime.now();
            private int callCount = 0;

            public LocalDateTime now() {
                callCount++;
                if (callCount <= 3) {
                    return baseTime;
                } else {
                    return baseTime.plusSeconds(301);
                }
            }
        };
    }

    private static GiftCardSystemFacade systemFacade() {
        return systemFacade(new Clock());
    }

    private static GiftCardSystemFacade systemFacade(Clock clock) {
        Map<String, User> users = new HashMap<>();
        Map<String, GiftCard> giftCards = new HashMap<>();
        Map<String, String> merchantsByKey = new HashMap<>();

        // Pre-populate with test data
        User u1 = new User("u1", "Manuel", "mramirezsilva@udesa.edu.ar", "Pinamar123");
        User u2 = new User("u2", "Tomas", "tdiaz@udesa.edu.ar", "CampoGrande2025");
        users.put(u1.getId(), u1);
        users.put(u2.getId(), u2);

        GiftCard g1 = new GiftCard("GC-1001", "u1", clock);
        g1.load(new BigDecimal("1000.00"), "SYSTEM", "initial load");
        GiftCard g2 = new GiftCard("GC-2001", "u2", clock);
        g2.load(new BigDecimal("500.00"), "SYSTEM", "initial load");
        giftCards.put(g1.getCode(), g1);
        giftCards.put(g2.getCode(), g2);

        merchantsByKey.put("merchant-key-abc", "m-abc");
        merchantsByKey.put("merchant-key-xyz", "m-xyz");
        merchantsByKey.put("merchant-key-123", "m-123");

        return new GiftCardSystemFacade(users, giftCards, merchantsByKey, clock);
    }

    private void assertThrowsLike(Executable executable, String message) {
        assertEquals(message,
                assertThrows(Exception.class, executable)
                        .getMessage());
    }
}