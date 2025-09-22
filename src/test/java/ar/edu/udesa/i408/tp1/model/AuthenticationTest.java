package ar.edu.udesa.i408.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.function.Executable;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {
    private Authentication auth;
    private Map<String, User> users;

    @BeforeEach
    public void setUp() {
        users = new HashMap<>();
        User user1 = new User("testUser1", "Test User 1", "test1@test.com", "password123");
        User user2 = new User("testUser2", "Test User 2", "test2@test.com", "password456");
        users.put("testUser1", user1);
        users.put("testUser2", user2);

        Clock clock = new Clock();
        auth = new Authentication(users, clock);
    }

    @Test
    public void test01loginWithValidCredentialsReturnsNotNUllToken(){
        Session token = auth.login("testUser1", "password123");
        assertNotNull(token.getToken());
    }

    @Test
    public void test02loginWithUserThatDoesNotExistsFails(){
        assertThrowsLike(() -> auth.login("notestUser1", "password123"), Authentication.userNotFoundErrorDescription);
    }

    @Test
    public void test03loginWithInvalidPasswordFails(){
        assertThrowsLike( () -> auth.login("testUser1", "invalidpassword123"), Authentication.passwordsDoNotMatchErrorDescription);

    }

    @Test
    public void test04loginWithUsernameWithDifferentCaseIsInvalid(){
        assertThrowsLike( () -> auth.login("TestUser1", "password123"), Authentication.userNotFoundErrorDescription);
    }

    @Test
    public void test05loginWithEmptyUsernameFails(){
        assertThrowsLike( () -> auth.login("", "password123"), Authentication.invalidInputErrorDescription);
    }

    @Test
    public void test06loginWithEmptyPasswordFails(){
        assertThrowsLike( () -> auth.login("testUser1", ""), Authentication.invalidInputErrorDescription);
    }

    @Test
    public void test07loginWithNullUsernameFails(){
        assertThrowsLike( () -> auth.login(null, "password123"), Authentication.invalidInputErrorDescription);
    }

    @Test
    public void test08loginWithNullPasswordFails(){
        assertThrowsLike( () -> auth.login("testUser1", null), Authentication.invalidInputErrorDescription);
    }

    @Test
    public void test09loginWithMultipleUsersWorksAndReturnsDifferentTokens(){
        Session token1 = auth.login("testUser1", "password123");
        Session token2 = auth.login("testUser2", "password456");
        assertNotEquals(token1.getToken(), token2.getToken());
    }

    @Test
    public void test10loginWithSameUsersTwiceWorksAndReturnsDifferentTokens(){
        Session token1 = auth.login("testUser1", "password123");
        Session token2 = auth.login("testUser2", "password456");
        assertNotEquals(token1.getToken(), token2.getToken());
    }


    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}