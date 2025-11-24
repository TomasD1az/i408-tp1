package org.udesa.giftcards.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.giftcards.model.GiftCard;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GiftCardServiceTest extends ModelServiceTest<GiftCard, GiftCardService> {

    @Override
    protected GiftCard newSample() {
        return new GiftCard("GC" + System.nanoTime(), 100);
    }

    @Override
    protected GiftCard updateUser(GiftCard gc) {
        gc.setBalance(gc.getBalance() + 50);
        gc.setOwner("Bob");
        gc.getCharges().add("Updated charge");
        return gc;
    }

    @Test
    public void test01FindByCardIdFailsWhenNotFound() {
        assertThrows(RuntimeException.class,
                () -> service.findByCardId("noExiste"));
    }

    @Test
    public void test02FindByCardIdReturnsCorrectEntity() {
        GiftCard saved = savedSample();

        GiftCard found = service.findByCardId(saved.getCardId());

        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getBalance(), found.getBalance());
    }

    @Test
    public void test03ChargeCardUpdatesBalanceAndAddsCharge() {
        GiftCard saved = new GiftCard("CARDX", 200);
        saved.setOwner("Alice");
        saved = service.save(saved);

        GiftCard updated = service.chargeCard("CARDX", 50, "Compra de prueba");

        assertEquals(150, updated.getBalance());
        assertEquals(1, updated.getCharges().size());
        assertEquals("Compra de prueba", updated.getCharges().getFirst());
    }

    @Test
    public void test04RedeemCardAssignsOwnerOnlyIfNotOwned() {
        GiftCard saved = service.save(new GiftCard("CARDY", 300));

        GiftCard redeemed = service.redeemCard("CARDY", "Alice");

        assertTrue(redeemed.isOwnedBy("Alice"));

        assertThrows(RuntimeException.class,
                () -> service.redeemCard("CARDY", "Bob"));
    }

    @Test
    public void test05ChargeCardFailsWhenBalanceInsufficient() {
        GiftCard saved = service.save(new GiftCard("CARDZ", 20));
        saved.setOwner("Owner");
        service.save(saved);

        assertThrows(RuntimeException.class,
                () -> service.chargeCard("CARDZ", 50, "Intento inválido"));
    }

    @Test
    public void test06GetBalanceAndGetChargesReturnProjectionValues() {
        GiftCard saved = service.save(new GiftCard("CARDW", 500));
        saved.setOwner("Alice");
        saved.getCharges().add("Primera compra");
        saved.getCharges().add("Segunda compra");
        service.save(saved);

        assertEquals(500, service.getBalance("CARDW"));

        var charges = service.getCharges("CARDW");
        assertEquals(2, charges.size());
        assertEquals("Primera compra", charges.getFirst());
    }
}