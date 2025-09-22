package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    @Test
    public void test01newTokenIsValid() {
        Clock clock = new Clock();
        Session token = new Session("user1", clock);

        assertTrue(token.isValid());
        assertEquals("user1", token.getUserId());
        assertNotNull(token.getToken());
    }

    @Test
    public void test02tokensHaveUniqueIds() {
        Clock clock = new Clock();
        Session token1 = new Session("user1", clock);
        Session token2 = new Session("user2", clock);

        assertNotEquals(token1.getToken(), token2.getToken());
    }

    @Test
    public void test03tokenExpiresAfterTimePasses(){
        LocalDateTime initialTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Clock mockClock = Mockito.mock(Clock.class);
        Mockito.when(mockClock.now()).thenReturn(initialTime);

        Session session = new Session("user1", mockClock);
        assertTrue(session.isValid());

        LocalDateTime expirationTime = initialTime.plusMinutes(6);
        Mockito.when(mockClock.now()).thenReturn(expirationTime);

        assertFalse(session.isValid());
    }

    @Test
    public void test04tokenUpdatesAndInitialTImeChanges(){
        LocalDateTime initialTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Clock mockClock = Mockito.mock(Clock.class);
        Mockito.when(mockClock.now()).thenReturn(initialTime);

        Session session = new Session("user1", mockClock);
        assertEquals(initialTime, session.getCreationTime());

        Mockito.when(mockClock.now()).thenReturn(initialTime.plusMinutes(1));
        session.update();
        assertEquals(initialTime.plusMinutes(1), session.getCreationTime());
    }

    @Test
    public void test05tokenIfUpdatesDoesNotExpireInFiveMinutes(){
        LocalDateTime initialTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Clock mockClock = Mockito.mock(Clock.class);
        Mockito.when(mockClock.now()).thenReturn(initialTime);

        Session session = new Session("user1", mockClock);
        assertTrue(session.isValid());

        Mockito.when(mockClock.now()).thenReturn(initialTime.plusMinutes(3));
        session.update();

        Mockito.when(mockClock.now()).thenReturn(initialTime.plusMinutes(5));
        assertTrue(session.isValid());
    }
}