package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    @Test
    public void newTokenIsValid() {
        Clock clock = new Clock();
        Session session = new Session("testUser", clock);

        assertTrue(session.isValid());
        assertEquals("testUser", session.getUserId());
        assertNotNull(session.getToken());
    }

    @Test
    public void tokensHaveUniqueIds() {
        Clock clock = new Clock();
        Session session1 = new Session("user1", clock);
        Session session2 = new Session("user2", clock);

        assertNotEquals(session1.getToken(), session2.getToken());
    }
}