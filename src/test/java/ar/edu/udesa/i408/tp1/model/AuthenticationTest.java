package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    @Test
    public void canLoginWithValidCredentials() {
        Map<String, User> users = new HashMap<>();
        User user = new User("testUser", "Test User", "test@test.com", "password123");
        users.put("testUser", user);
        Clock clock = new Clock();
        Authentication auth = new Authentication(users, clock);

        Token token = auth.login("testUser", "password123");

        assertNotNull(token);
        assertEquals("testUser", token.getUserId());
        assertTrue(token.isValid());
    }

    @Test
    public void canNotLoginWithInvalidUser() {
        Map<String, User> users = new HashMap<>();
        User user = new User("testUser", "Test User", "test@test.com", "password123");
        users.put("testUser", user);
        Clock clock = new Clock();
        Authentication auth = new Authentication(users, clock);

        try {
            auth.login("invalidUser", "password123");
            fail("Should have thrown exception for invalid user");
        } catch (RuntimeException e) {
            assertEquals(Authentication.USER_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    public void canNotLoginWithInvalidPassword() {
        Map<String, User> users = new HashMap<>();
        User user = new User("testUser", "Test User", "test@test.com", "password123");
        users.put("testUser", user);
        Clock clock = new Clock();
        Authentication auth = new Authentication(users, clock);

        try {
            auth.login("testUser", "wrongPassword");
            fail("Should have thrown exception for invalid password");
        } catch (RuntimeException e) {
            assertEquals(Authentication.PASSWORDS_DO_NOT_MATCH, e.getMessage());
        }
    }
}