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
    public void test01CanClaimGiftCardsWithValidToken() {
        String token = systemFacade.login("u1", "Pinamar123");
        List<GiftCard> claimedCards = systemFacade.claimGiftCards(token);

        assertEquals(1, claimedCards.size());
        assertEquals("GC-1001", claimedCards.get(0).getCode());
        assertEquals("u1", claimedCards.get(0).getOwnerId());
    }

    @Test
    public void test02CanNotClaimGiftCardsWithInvalidToken() {
        assertThrowsLike(() -> systemFacade.claimGiftCards("invalidToken"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test03CanCheckBalanceOfOwnedGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");

        assertEquals(new BigDecimal("1000.00"), balance);
    }

    @Test
    public void test04CanNotCheckBalanceOfNotOwnedGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getBalance(token, "GC-2001"), // This belongs to u2
                GiftCardSystemFacade.giftCardNotOwnedByUserErrorDescription);
    }

    @Test
    public void test05CanNotCheckBalanceWithInvalidToken() {
        assertThrowsLike(() -> systemFacade.getBalance("invalidToken", "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test06CanNotCheckBalanceOfInvalidGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getBalance(token, "INVALID-CODE"),
                GiftCardSystemFacade.invalidGiftCardErrorDescription);
    }

    @Test
    public void test07MerchantCanChargeGiftCard() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        BigDecimal chargeAmount = new BigDecimal("200.00");
        BigDecimal expectedBalance = new BigDecimal("800.00");

        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", chargeAmount, "test purchase");

        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");
        assertEquals(expectedBalance, balance);
    }

    @Test
    public void test08CanNotChargeMoreThanGiftCardBalance() {
        assertThrowsLike(() -> systemFacade.merchantCharge("merchant-key-abc", "GC-2001",
                        new BigDecimal("1500.00"), "overspend"),
                GiftCardSystemFacade.insufficientBalanceErrorDescription);
    }

    @Test
    public void test09CanNotChargeWithInvalidMerchantKey() {
        assertThrowsLike(() -> systemFacade.merchantCharge("invalid-key", "GC-1001",
                        new BigDecimal("100.00"), "test"),
                GiftCardSystemFacade.invalidMerchantErrorDescription);
    }

    @Test
    public void test10CanNotChargeInvalidGiftCard() {
        assertThrowsLike(() -> systemFacade.merchantCharge("merchant-key-abc", "INVALID-CODE",
                        new BigDecimal("100.00"), "test"),
                GiftCardSystemFacade.invalidGiftCardErrorDescription);
    }

    @Test
    public void test11TransactionHistoryIsUpdatedOnCharge() {
        String token = systemFacade.login("u1", "Pinamar123");
        int initialTransactions = systemFacade.getTransactionHistory(token, "GC-1001").size();

        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", new BigDecimal("100.00"), "test purchase");

        List<Transaction> transactions = systemFacade.getTransactionHistory(token, "GC-1001");
        assertEquals(initialTransactions + 1, transactions.size());

        Transaction lastTransaction = transactions.get(transactions.size() - 1);
        assertEquals(new BigDecimal("100.00"), lastTransaction.getAmount().abs());
        assertEquals("test purchase", lastTransaction.getDescription());
    }

    @Test
    public void test12CanNotGetTransactionHistoryOfNotOwnedGiftCard() {
        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getTransactionHistory(token, "GC-2001"), // This belongs to u2
                GiftCardSystemFacade.giftCardNotOwnedByUserErrorDescription);
    }

    @Test
    public void test13CanNotGetTransactionHistoryWithInvalidToken() {
        assertThrowsLike(() -> systemFacade.getTransactionHistory("invalidToken", "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }


    @Test
    public void test14CanNotUseOperationsWithExpiredToken() {
        GiftCardSystemFacade systemFacade = systemFacade(expiredTokenClock());

        String token = systemFacade.login("u1", "Pinamar123");
        assertThrowsLike(() -> systemFacade.claimGiftCards(token),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test15CanNotCheckBalanceWithExpiredToken() {
        GiftCardSystemFacade systemFacade = systemFacade(expiredTokenClock());

        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getBalance(token, "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test16CanNotGetTransactionHistoryWithExpiredToken() {
        GiftCardSystemFacade systemFacade = systemFacade(expiredTokenClock());

        String token = systemFacade.login("u1", "Pinamar123");

        assertThrowsLike(() -> systemFacade.getTransactionHistory(token, "GC-1001"),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test17LogoutRemovesTokenFromSystem() {
        String token = systemFacade.login("u1", "Pinamar123");

        List<GiftCard> cards = systemFacade.claimGiftCards(token);
        assertEquals(1, cards.size());

        systemFacade.logout(token);

        assertThrowsLike(() -> systemFacade.claimGiftCards(token),
                GiftCardSystemFacade.invalidTokenErrorDescription);
    }

    @Test
    public void test18MultipleUsersCanHaveValidTokensSimultaneously() {
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
    public void test19MultipleChargesReduceBalanceCorrectly() {
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
    public void test20DifferentMerchantsCanChargeGiftCard() {
        systemFacade.merchantCharge("merchant-key-abc", "GC-1001", new BigDecimal("100.00"), "merchant abc");
        systemFacade.merchantCharge("merchant-key-xyz", "GC-1001", new BigDecimal("150.00"), "merchant xyz");
        systemFacade.merchantCharge("merchant-key-123", "GC-1001", new BigDecimal("50.00"), "merchant 123");

        String token = systemFacade.login("u1", "Pinamar123");
        BigDecimal balance = systemFacade.getBalance(token, "GC-1001");
        assertEquals(new BigDecimal("700.00"), balance);

        List<Transaction> transactions = systemFacade.getTransactionHistory(token, "GC-1001");
        assertTrue(transactions.size() >= 3);
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