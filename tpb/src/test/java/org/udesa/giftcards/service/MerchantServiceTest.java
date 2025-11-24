package org.udesa.giftcards.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.giftcards.model.Merchant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MerchantServiceTest extends ModelServiceTest<Merchant, MerchantService> {

    @Autowired
    MerchantService service;

    @Override
    protected Merchant newSample() {
        return new Merchant("M" + System.nanoTime());
    }

    @Override
    protected Merchant updateUser(Merchant merchant) {
        merchant.setCode(merchant.getCode() + "_updated");
        return merchant;
    }

    @Test
    public void test01CannotSaveTwoMerchantsWithSameCode() {
        service.save(new Merchant("M100"));

        assertThrows(Exception.class, () -> {
            service.save(new Merchant("M100"));
        });
    }

    @Test
    public void test02UpdateDataChangesCode() {
        Merchant m = service.save(new Merchant("OLD"));

        Merchant update = new Merchant("NEW");
        update.setId(m.getId());

        service.save(update);

        Merchant retrieved = service.getById(m.getId());
        assertEquals("NEW", retrieved.getCode());
    }

    @Test
    public void test03ExistsByCodeAfterUpdate() {
        Merchant m = service.save(new Merchant("A"));

        Merchant update = new Merchant("B");
        update.setId(m.getId());
        service.save(update);

        assertFalse(service.existsByCode("A"));
        assertTrue(service.existsByCode("B"));
    }
}