package org.udesa.giftcards.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.giftcards.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest extends ModelServiceTest<User, UserService> {

    @Autowired
    UserService service;

    @Override
    protected User newSample() {
        return new User("U" + System.nanoTime(), "pass123");
    }

    @Override
    protected User updateUser(User user) {
        user.setUsername(user.getUsername() + "_updated");
        user.setPassword("newPass123");
        return user;
    }

    @Test
    public void test01FindByUsernameSuccess() {
        User u = service.save(new User("alex", "abc"));
        User retrieved = service.findByUsername("alex");

        assertNotNull(retrieved);
        assertEquals("alex", retrieved.getUsername());
        assertEquals(u.getId(), retrieved.getId());
    }

    @Test
    public void test02FindByUsernameFailure() {
        assertThrows(RuntimeException.class, () -> {
            service.findByUsername("no_such_user");
        });
    }

    @Test
    public void test03CannotSaveTwoUsersWithSameUsername() {
        service.save(new User("bob", "x1"));

        assertThrows(Exception.class, () -> {
            service.save(new User("bob", "x2"));
        });
    }

    @Test
    public void test04UpdateDataChangesUsernameAndPassword() {
        User u = service.save(new User("carl", "oldPass"));

        User update = new User("carl_new", "newPass");
        update.setId(u.getId());

        service.save(update);

        User retrieved = service.getById(u.getId());
        assertEquals("carl_new", retrieved.getUsername());
        assertEquals("newPass", retrieved.getPassword());
    }

    @Test
    public void test05FindByUsernameReflectsUpdatedUser() {
        User u = service.save(new User("john", "123"));

        User update = new User("johnny", "456");
        update.setId(u.getId());
        service.save(update);

        assertThrows(RuntimeException.class,
                () -> service.findByUsername("john"));

        User refreshed = service.findByUsername("johnny");
        assertEquals(update.getId(), refreshed.getId());
    }
}