package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    @Test
    public void newTokenIsValid() {
        Clock clock = new Clock();
        Token token = new Token("testUser", clock);

        assertTrue(token.isValid());
        assertEquals("testUser", token.getUserId());
        assertNotNull(token.getToken());
    }

    @Test
    public void tokensHaveUniqueIds() {
        Clock clock = new Clock();
        Token token1 = new Token("user1", clock);
        Token token2 = new Token("user2", clock);

        assertNotEquals(token1.getToken(), token2.getToken());
    }
}