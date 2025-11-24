package org.udesa.giftcards.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.udesa.giftcards.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repo;

    @Test
    public void testFindByUsernameSuccess() {
        User u = new User("Bob", "123");
        repo.save(u);

        var found = repo.findByUsername("Bob");

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getUsername());
    }

    @Test
    public void testFindByUsernameReturnsEmpty() {
        var found = repo.findByUsername("Ghost");

        assertTrue(found.isEmpty());
    }
}