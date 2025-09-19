package ar.edu.udesa.i408.tp1.model;

import java.math.BigDecimal;
import java.util.*;

public class GiftCardSystemFacade {
    public static String invalidUserErrorDescription = "Invalid user id";
    public static String invalidGiftCardErrorDescription = "Invalid gift card code";
    public static String insufficientBalanceErrorDescription = "Not enough balance";
    public static String invalidMerchantErrorDescription = "Invalid merchant key";

    private final Map<String, User> users;
    private final Map<String, GiftCard> giftCards;
    private final Map<String, String> merchantsByKey; // merchantKey -> merchantId
    private final Map<String, Token> tokens; // tokenString -> Token

    public GiftCardSystemFacade() {
        this.users = new HashMap<>();
        this.giftCards = new HashMap<>();
        this.merchantsByKey = new HashMap<>();
        this.tokens = new HashMap<>();
        preloadData();
    }

    private void preloadData() {
        // ejemplo de precarga: usuarios
        User u1 = new User("u1", "Manuel", "mramirezsilva@udesa.edu.ar", "Pinamar123");
        User u2 = new User("u2", "Tomas", "tdiaz@udesa.edu.ar", "CampoGrande2025");
        User u3 = new User("u3", "Tron", "tron@defender.com", "DefendTheUser");
        User u4 = new User("u4", "CLU", "clu@perfectsystem.com", "EndOfLine");
        users.put(u1.getId(), u1);
        users.put(u2.getId(), u2);
        users.put(u3.getId(), u2);
        users.put(u4.getId(), u4);

        // tarjetas
        GiftCard g1 = new GiftCard("GC-1001", u1.getId());
        g1.load(new BigDecimal("1000.00"), "SYSTEM", "initial load");
        GiftCard g2 = new GiftCard("GC-2001", u2.getId());
        g2.load(new BigDecimal("500.00"), "SYSTEM", "initial load");
        GiftCard g3 = new GiftCard("GC-3001", u3.getId());
        g3.load(new BigDecimal("3000.00"), "SYSTEM", "initial load");
        GiftCard g4 = new GiftCard("GC-4001", u4.getId());
        g4.load(new BigDecimal("4000.00"), "SYSTEM", "initial load");
        giftCards.put(g1.getCode(), g1);
        giftCards.put(g2.getCode(), g2);
        giftCards.put(g3.getCode(), g3);
        giftCards.put(g4.getCode(), g4);

        // merchants: clave pública (merchantKey) -> merchantId
        merchantsByKey.put("merchant-key-abc", "m-abc");
        merchantsByKey.put("merchant-key-xyz", "m-xyz");
        merchantsByKey.put("merchant-key-123", "m-123");
        merchantsByKey.put("merchant-key-456", "m-456");
    }

    // AUTH helper
    public void storeToken(Token token) {
        tokens.put(token.getToken(), token);
    }

    public Optional<String> usernameForToken(String tokenString) {
        Token t = tokens.get(tokenString);
        if (t == null) return Optional.empty();
        if (!t.isValid()) {
            tokens.remove(tokenString);
            return Optional.empty();
        }
        return Optional.of(t.getUsername());
    }

    // LISTAR giftcards de un usuario
    public List<GiftCard> giftCardsOfUser(String userId) {
        checkValidUser(userId);
        List<GiftCard> res = new ArrayList<>();
        for (GiftCard gc : giftCards.values()) {
            if (gc.getOwnerId().equals(userId)) res.add(gc);
        }
        return res;
    }

    // CONSULTAR SALDO
    public BigDecimal getBalance(String giftCardCode) {
        GiftCard gc = giftCardIdentifiedAs(giftCardCode);
        return gc.getBalance();
    }

    // CONSULTAR DETALLE
    public List<Transaction> getTransactions(String giftCardCode) {
        GiftCard gc = giftCardIdentifiedAs(giftCardCode);
        return gc.getTransactionHistory();
    }

    // CARGAR / SPEND triggered by merchant using merchantKey
    public void merchantCharge(String merchantKey, String giftCardCode, BigDecimal amount, String description) {
        String merchantId = merchantsByKey.get(merchantKey);
        if (merchantId == null) throw new RuntimeException(invalidMerchantErrorDescription);
        GiftCard gc = giftCardIdentifiedAs(giftCardCode);
        boolean ok = gc.spend(amount, merchantId, description);
        if (!ok) throw new RuntimeException(insufficientBalanceErrorDescription);
    }

    // helper checks
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

    // util: crear merchant (para tests)
    public void addMerchant(String merchantKey, String merchantId) {
        merchantsByKey.put(merchantKey, merchantId);
    }

    // util: issue gift card to user
    public void issueGiftCard(String code, String ownerId, BigDecimal initialAmount) {
        checkValidUser(ownerId);
        GiftCard gc = new GiftCard(code, ownerId);
        if (initialAmount != null && initialAmount.compareTo(BigDecimal.ZERO) > 0) {
            gc.load(initialAmount, "SYSTEM", "initial load");
        }
        giftCards.put(code, gc);
    }
}