package org.udesa.giftcards.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.udesa.giftcards.model.Merchant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MerchantRepositoryTest {

    @Autowired
    private MerchantRepository repo;

    @Test
    public void testFindByCodeSuccess() {
        Merchant m = new Merchant("M1");
        repo.save(m);

        var found = repo.findByCode("M1");

        assertTrue(found.isPresent());
        assertEquals("M1", found.get().getCode());
    }

    @Test
    public void testFindByCodeReturnsEmptyWhenNotExists() {
        var found = repo.findByCode("NOPE");

        assertTrue(found.isEmpty());
    }
}