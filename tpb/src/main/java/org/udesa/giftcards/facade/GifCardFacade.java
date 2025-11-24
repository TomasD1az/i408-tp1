package org.udesa.giftcards.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udesa.giftcards.model.*;
import org.udesa.giftcards.service.GiftCardService;
import org.udesa.giftcards.service.MerchantService;
import org.udesa.giftcards.service.UserService;

@Service
public class GifCardFacade {
    public static final String InvalidUser = "InvalidUser";
    public static final String InvalidMerchant = "InvalidMerchant";
    public static final String InvalidToken = "InvalidToken";

    private Clock clock;
    private UserService userService;
    private GiftCardService giftCardService;
    private MerchantService merchantService;

    private Map<UUID, UserSession> sessions = new HashMap<>();

    @Autowired
    public GifCardFacade(Clock clock, UserService userService, GiftCardService giftCardService,
            MerchantService merchantService) {
        this.clock = clock;
        this.userService = userService;
        this.giftCardService = giftCardService;
        this.merchantService = merchantService;
    }

    public UUID login(String userKey, String pass) {
        User user = userService.findByUsername(userKey);
        if (!user.getPassword().equals(pass)) {
            throw new RuntimeException(InvalidUser);
        }

        UUID token = UUID.randomUUID();
        sessions.put(token, new UserSession(userKey, clock));
        return token;
    }

    public void redeem(UUID token, String cardId) {
        String owner = findUser(token);
        giftCardService.redeemCard(cardId, owner);
    }

    public int balance(UUID token, String cardId) {
        GiftCard card = ownedCard(token, cardId);
        return card.getBalance();
    }

    public void charge(String merchantKey, String cardId, int amount, String description) {
        merchantService.findByCode(merchantKey);
        giftCardService.chargeCard(cardId, amount, description);
    }

    public List<String> details(UUID token, String cardId) {
        ownedCard(token, cardId);
        return giftCardService.getCharges(cardId);
    }

    private GiftCard ownedCard(UUID token, String cardId) {
        GiftCard card = giftCardService.findByCardId(cardId);
        if (!card.isOwnedBy(findUser(token)))
            throw new RuntimeException(InvalidToken);
        return card;
    }

    private String findUser(UUID token) {
        return sessions.computeIfAbsent(token, key -> {
            throw new RuntimeException(InvalidToken);
        })
                .userAliveAt(clock);
    }
}