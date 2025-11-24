package org.udesa.giftcards.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udesa.giftcards.model.GiftCard;
import org.udesa.giftcards.repository.GiftCardRepository;

@Service
public class GiftCardService extends ModelService<GiftCard, GiftCardRepository> {

    protected void updateData(GiftCard existingObject, GiftCard updatedObject) {
        existingObject.setCardId(updatedObject.getCardId());
        existingObject.setBalance(updatedObject.getBalance());
        existingObject.setOwner(updatedObject.getOwner());
        existingObject.setCharges(updatedObject.getCharges());
    }

    @Transactional(readOnly = true)
    public GiftCard findByCardId(String cardId) {
        return repository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException(GiftCard.InvalidCard));
    }

    @Transactional
    public GiftCard chargeCard(String cardId, int amount, String description) {
        GiftCard card = findByCardId(cardId);
        card.charge(amount, description);
        return save(card);
    }

    @Transactional
    public GiftCard redeemCard(String cardId, String owner) {
        GiftCard card = findByCardId(cardId);
        card.redeem(owner);
        return save(card);
    }

    @Transactional(readOnly = true)
    public List<String> getCharges(String cardId) {
        GiftCard card = findByCardId(cardId);
        return card.getCharges();
    }

    @Transactional(readOnly = true)
    public int getBalance(String cardId) {
        GiftCard card = findByCardId(cardId);
        return card.getBalance();
    }
}

