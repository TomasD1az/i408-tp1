package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GiftCardSystemFacade {
    public static String invalidUserErrorDescription = "Invalid user id";
    public static String invalidGiftCardErrorDescription = "Invalid gift card code";
    public static String insufficientBalanceErrorDescription = "Not enough balance";

    private final Map<String, User> users;
    private final Map<String, GiftCard> giftCards;

    public GiftCardSystemFacade() {
        this.users = new HashMap<>();
        this.giftCards = new HashMap<>();
    }

    public void registerUser(String id, String name, String email) { users.put(id, new User(id, name, email));}

    public void issueGiftCard(String code, String ownerId) {
        checkValidUser(ownerId);
        giftCards.put(code, new GiftCard(code, ownerId));
    }

    public void loadGiftCard(String code, BigDecimal amount) {
        GiftCard giftCard = giftCardIdentifiedAs(code);
        giftCard.load(amount);
    }

    public void spendFromGiftCard(String code, BigDecimal amount) {
        GiftCard giftCard = giftCardIdentifiedAs(code);
        if (!giftCard.spend(amount)) {
            throw new RuntimeException(insufficientBalanceErrorDescription);
        }
    }

    public BigDecimal checkBalance(String code) { return giftCardIdentifiedAs(code).getBalance();}

    private void checkValidUser(String id) {
        if (!users.containsKey(id)) {
            throw new RuntimeException(invalidUserErrorDescription);
        }
    }

    private GiftCard giftCardIdentifiedAs(String code) {
        GiftCard giftCard = giftCards.get(code);
        if (giftCard == null) {
            throw new RuntimeException(invalidGiftCardErrorDescription);
        }
        return giftCard;
    }
}