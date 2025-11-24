package org.udesa.giftcards.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.udesa.giftcards.model.GiftCard;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GiftCardRepositoryTest {

    @Autowired
    private GiftCardRepository repo;

    @Test
    public void testFindByCardIdSuccess() {
        GiftCard gc = new GiftCard("GC1", 50);
        repo.save(gc);

        var found = repo.findByCardId("GC1");

        assertTrue(found.isPresent());
        assertEquals("GC1", found.get().getCardId());
    }

    @Test
    public void testFindByCardIdReturnsEmpty() {
        var found = repo.findByCardId("NO_CARD");

        assertTrue(found.isEmpty());
    }
}