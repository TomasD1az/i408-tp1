package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.util.*;

public class GiftCardSystemFacade {
    public static String invalidTokenErrorDescription = "Invalid or expired token";
    public static String invalidGiftCardErrorDescription = "Invalid gift card code";
    public static String insufficientBalanceErrorDescription = "Not enough balance";
    public static String invalidMerchantErrorDescription = "Invalid merchant key";
    public static String giftCardNotOwnedByUserErrorDescription = "Gift card not owned by user";

    private final Map<String, User> users;
    private final Map<String, GiftCard> giftCards;
    private final Map<String, String> merchantsByKey; // merchantKey -> merchantId
    private final Map<String, Session> tokens; // tokenString -> Token
    private final Authentication authentication;
    private final Clock clock;

    public GiftCardSystemFacade() {
        this.users = new HashMap<>();
        this.giftCards = new HashMap<>();
        this.merchantsByKey = new HashMap<>();
        this.tokens = new HashMap<>();
        this.clock = new Clock();
        this.authentication = new Authentication(users, clock);
        preloadData();
    }

    // For testing - constructor with dependencies (following TusLibros pattern)
    public GiftCardSystemFacade(Map<String, User> users, Map<String, GiftCard> giftCards,
                                Map<String, String> merchantsByKey, Clock clock) {
        this.users = users;
        this.giftCards = giftCards;
        this.merchantsByKey = merchantsByKey;
        this.tokens = new HashMap<>();
        this.clock = clock;
        this.authentication = new Authentication(users, clock);
    }

    private void preloadData() {
        // Users
        User u1 = new User("u1", "Manuel", "mramirezsilva@udesa.edu.ar", "Pinamar123");
        User u2 = new User("u2", "Tomas", "tdiaz@udesa.edu.ar", "CampoGrande2025");
        User u3 = new User("u3", "Tron", "tron@defender.com", "DefendTheUser");
        User u4 = new User("u4", "CLU", "clu@perfectsystem.com", "EndOfLine");
        users.put(u1.getId(), u1);
        users.put(u2.getId(), u2);
        users.put(u3.getId(), u3);
        users.put(u4.getId(), u4);

        // Gift Cards
        GiftCard g1 = new GiftCard("GC-1001", "u1", clock);
        g1.load(new BigDecimal("1000.00"), "SYSTEM", "initial load");
        GiftCard g2 = new GiftCard("GC-2001", "u2", clock);
        g2.load(new BigDecimal("500.00"), "SYSTEM", "initial load");
        GiftCard g3 = new GiftCard("GC-3001", "u3", clock);
        g3.load(new BigDecimal("3000.00"), "SYSTEM", "initial load");
        GiftCard g4 = new GiftCard("GC-4001", "u4", clock);
        g4.load(new BigDecimal("4000.00"), "SYSTEM", "initial load");

        giftCards.put(g1.getCode(), g1);
        giftCards.put(g2.getCode(), g2);
        giftCards.put(g3.getCode(), g3);
        giftCards.put(g4.getCode(), g4);

        // Merchants
        merchantsByKey.put("merchant-key-abc", "m-abc");
        merchantsByKey.put("merchant-key-xyz", "m-xyz");
        merchantsByKey.put("merchant-key-123", "m-123");
        merchantsByKey.put("merchant-key-456", "m-456");
    }

    // Authentication operations - following TusLibros validation pattern
    public String login(String userId, String password) {
        // Let Authentication handle validation and throw appropriate exceptions
        Session token = authentication.login(userId, password);
        tokens.put(token.getToken(), token);
        return token.getToken();
    }

    public void logout(String tokenString) {
        // Simply remove the token - no need to force expiration
        tokens.remove(tokenString);
    }

    // Gift card operations (require valid token)
    public List<GiftCard> claimGiftCards(String tokenString) {
        String userId = validateTokenAndGetUserId(tokenString);
        return giftCardsOwnedBy(userId);
    }

    public BigDecimal getBalance(String tokenString, String giftCardCode) {
        String userId = validateTokenAndGetUserId(tokenString);
        GiftCard giftCard = giftCardOwnedBy(userId, giftCardCode);
        return giftCard.getBalance();
    }

    public List<Transaction> getTransactionHistory(String tokenString, String giftCardCode) {
        String userId = validateTokenAndGetUserId(tokenString);
        GiftCard giftCard = giftCardOwnedBy(userId, giftCardCode);
        return giftCard.getTransactionHistory();
    }

    // Merchant operations - following TusLibros merchant validation pattern
    public void merchantCharge(String merchantKey, String giftCardCode, BigDecimal amount, String description) {
        String merchantId = validateMerchantKey(merchantKey);
        GiftCard giftCard = giftCardIdentifiedAs(giftCardCode);

        if (!giftCard.spend(amount, merchantId, description)) {
            throw new RuntimeException(insufficientBalanceErrorDescription);
        }
    }

    // Validation methods following TusLibros patterns
    private String validateTokenAndGetUserId(String tokenString) {
        Session token = tokenIdentifiedAs(tokenString);
        checkTokenIsActive(token, tokenString);
        return token.getUserId(); // Fixed: use getUserId() instead of getUsername()
    }

    private Session tokenIdentifiedAs(String tokenString) {
        Session token = tokens.get(tokenString);
        if (token == null) {
            throw new RuntimeException(invalidTokenErrorDescription);
        }
        return token;
    }

    private void checkTokenIsActive(Session session, String tokenString) {
        boolean isValid = session.isValid();
        if (!isValid) {
            tokens.remove(tokenString);
            throw new RuntimeException(invalidTokenErrorDescription);
        }
    }

    // Following TusLibros merchant validation pattern
    private String validateMerchantKey(String merchantKey) {
        String merchantId = merchantsByKey.get(merchantKey);
        if (merchantId == null) {
            throw new RuntimeException(invalidMerchantErrorDescription);
        }
        return merchantId;
    }

    private List<GiftCard> giftCardsOwnedBy(String userId) {
        return giftCards.values().stream()
                .filter(gc -> gc.getOwnerId().equals(userId))
                .toList();
    }

    private GiftCard giftCardOwnedBy(String userId, String giftCardCode) {
        GiftCard giftCard = giftCardIdentifiedAs(giftCardCode);
        if (!giftCard.getOwnerId().equals(userId)) {
            throw new RuntimeException(giftCardNotOwnedByUserErrorDescription);
        }
        return giftCard;
    }

    private GiftCard giftCardIdentifiedAs(String code) {
        GiftCard giftCard = giftCards.get(code);
        if (giftCard == null) {
            throw new RuntimeException(invalidGiftCardErrorDescription);
        }
        return giftCard;
    }

    public Clock clock() { return clock; }
    public Map<String, User> users() { return users; }
    public Map<String, GiftCard> giftCards() { return giftCards; }
    public Map<String, String> merchantsByKey() { return merchantsByKey; }
}