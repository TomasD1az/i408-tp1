package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    @Test
    public void newTokenIsValid() {
        Clock clock = new Clock();
        Session token = new Session("testUser", clock);

        assertTrue(token.isValid());
        assertEquals("testUser", token.getUserId());
        assertNotNull(token.getToken());
    }

    @Test
    public void tokensHaveUniqueIds() {
        Clock clock = new Clock();
        Session token1 = new Session("user1", clock);
        Session token2 = new Session("user2", clock);

        assertNotEquals(token1.getToken(), token2.getToken());
    }
}