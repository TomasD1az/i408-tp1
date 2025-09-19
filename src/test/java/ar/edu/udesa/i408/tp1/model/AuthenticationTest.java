package ar.edu.udesa.i408.tp1.model;


import org.junit.jupiter.api.Test;
import java.util.Map;
import org.junit.jupiter.api.function.Executable;


import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    private static final User defaultUserObj = new User("u1", "Test User", "user@mail.com", "password");
    private static final Map<String, User> defaultUser = Map.of("user", defaultUserObj);

    @Test
    public void test01loginWithValidCredentialsReturnsToken(){
        Authentication auth = new Authentication(defaultUser);
        Token token = auth.login("user", "password");
        assertNotNull(token);
    }

    @Test
    public void test02loginWithUnregisteredUserFails(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login("nouser", "password"), Authentication.USER_NOT_FOUND);
    }

    @Test
    public void test03loginWithInvalidPasswordFails(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login("user", "invalidpassword"), Authentication.PASSWORDS_DO_NOT_MATCH);
    }

    @Test
    public void test04loginConUsernameConDiferenteCaseFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login("User", "password"), Authentication.USER_NOT_FOUND);
    }

    @Test
    public void test05loginWithEmptyUsernameFails(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login("", "password"), Authentication.INVALID_INPUT);
    }

    @Test
    public void test06loginConContrasenaVacioFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login("user", ""), Authentication.INVALID_INPUT);
    }

    @Test
    public void test07loginConUsuarioNullFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login(null, "password"), Authentication.INVALID_INPUT);
    }

    @Test
    public void test08loginConContrasenaNullFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike(() -> auth.login("user", null), Authentication.INVALID_INPUT);
    }

    @Test
    public void test09loginConVariasCuentasDaTokensDistintos(){
        Map<String, User> users = Map.of(
                "user1", new User("u1", "Alice", "alice@mail.com", "pass1"),
                "user2", new User("u2", "Bob", "bob@mail.com", "pass2")
        );
        Authentication auth = new Authentication(users);

        Token t1 = auth.login("user1", "pass1");
        Token t2 = auth.login("user2", "pass2");

        assertNotEquals(t1.getToken(), t2.getToken());
    }

    @Test
    public void test10loginConMismoUsuarioDosVecesEnMenosDe5MinDevuelveTokensDiferentes(){
        Authentication auth = new Authentication(defaultUser);
        Token t1 = auth.login("user", "password");
        Token t2 = auth.login("user", "password");

        assertNotEquals(t1.getToken(), t2.getToken());
    }

    @Test
    public void test11loginConMismoUsuarioVariasVecesEnMasDe5MinutosDevuelveTokensDiferentes() throws InterruptedException {
        Authentication auth = new Authentication(defaultUser);

        Token t1 = auth.login("user", "password");
        // Simular expiración manual: forzamos expiración en el Token
        t1.forceExpiration();

        Token t2 = auth.login("user", "password");
        assertNotEquals(t1.getToken(), t2.getToken());
    }

    @Test
    public void test12loginConCadaUsuarioDeUnMapaValidoDevuelveToken(){
        Map<String, User> users = Map.of(
                "user1", new User("u1", "Alice", "alice@mail.com", "pass1"),
                "user2", new User("u2", "Bob", "bob@mail.com", "pass2"),
                "user3", new User("u3", "Charlie", "charlie@mail.com", "pass3")
        );
        Authentication auth = new Authentication(users);

        assertNotNull(auth.login("user1", "pass1"));
        assertNotNull(auth.login("user2", "pass2"));
        assertNotNull(auth.login("user3", "pass3"));
    }

    private void assertThrowsLike(Executable executable, String message) {
        assertEquals(message,
                assertThrows(Exception.class, executable).getMessage());
    }
}