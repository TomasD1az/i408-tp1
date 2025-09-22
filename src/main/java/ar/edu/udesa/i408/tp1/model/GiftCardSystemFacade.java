package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.util.*;

public class GiftCardSystemFacade {
    public static String invalidTokenErrorDescription = "Invalid or expired token";
    public static String invalidGiftCardErrorDescription = "Invalid gift card code";
    public static String insufficientBalanceErrorDescription = "Not enough balance";
    public static String invalidMerchantErrorDescription = "Invalid merchant key";
    public static String giftCardNotOwnedByUserErrorDescription = "Gift card not owned by user";

    private Map<String, User> users;
    private Map<String, GiftCard> giftCards;
    private Map<String, String> merchantsByKey;
    private Map<String, Session> tokens;
    private Authentication authentication;
    private Clock clock;

    public GiftCardSystemFacade(Map<String, User> users, Map<String, GiftCard> giftCards,
                                Map<String, String> merchantsByKey, Clock clock) {
        this.users = users;
        this.giftCards = giftCards;
        this.merchantsByKey = merchantsByKey;
        this.tokens = new HashMap<>();
        this.clock = clock;
        this.authentication = new Authentication(users, clock);
    }

    public String login(String userId, String password) {
        Session token = authentication.login(userId, password);
        tokens.put(token.getToken(), token);
        return token.getToken();
    }

    public void logout(String tokenString) {
        tokens.remove(tokenString);
    }

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

    public void merchantCharge(String merchantKey, String giftCardCode, BigDecimal amount, String description) {
        String merchantId = validateMerchantKey(merchantKey);
        GiftCard giftCard = giftCardIdentifiedAs(giftCardCode);

        if (!giftCard.spend(amount, merchantId, description)) {
            throw new RuntimeException(insufficientBalanceErrorDescription);
        }
    }

    private String validateTokenAndGetUserId(String tokenString) {
        Session token = tokenIdentifiedAs(tokenString);
        checkTokenIsActive(token, tokenString);
        return token.getUserId();
    }

    private Session tokenIdentifiedAs(String tokenString) {
        Session token = tokens.get(tokenString);
        if (token == null) {
            throw new RuntimeException(invalidTokenErrorDescription);
        }
        return token;
    }

    private void checkTokenIsActive(Session token, String tokenString) {
        boolean isValid = token.isValid();
        if (!isValid) {
            tokens.remove(tokenString);
            throw new RuntimeException(invalidTokenErrorDescription);
        }
    }

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
}