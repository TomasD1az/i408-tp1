package ar.edu.udesa.i408.tp1;


import org.junit.jupiter.api.Test;
import java.util.Map;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {
    private static final Map<String, String> defaultUsers = Map.of( "user1",  "password1", "user2", "password2", "user3", "password3");

    //tambien es raro, comparte con tokentest, preguntar
    @Test
    public void test01loginConCredencialesValidasDevuelveTokenNoNulo(){
        Authentication auth = new Authentication(defaultUsers);
        Token token = auth.login("user1", "password1");
        assertNotNull(token.getToken());
    }

    @Test
    public void test02loginConUsuarioQueNoExisteFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login("nouser1", "password1"), Authentication.USER_NOT_FOUND);
    }

    @Test
    public void test03loginConContrasenaInvalidaFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login("user1", "invalidpassword1"), Authentication.PASSWORDS_DO_NOT_MATCH);

    }

    @Test
    public void test04loginConUsernameConDiferenteCaseFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login("User1", "password1"), Authentication.USER_NOT_FOUND);
    }

    @Test
    public void test05loginConUsernameVacioFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login("", "password1"), Authentication.INVALID_INPUT);
    }

    @Test
    public void test06loginConContrasenaVacioFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login("user1", ""), Authentication.INVALID_INPUT);
    }

    @Test
    public void test07loginConUsuarioNullFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login(null, "password1"), Authentication.INVALID_INPUT);
    }

    @Test
    public void test08loginConContrasenaNullFalla(){
        Authentication auth = new Authentication(defaultUsers);
        assertThrowsLike( () -> auth.login("user1", null), Authentication.INVALID_INPUT);
    }

    //preguntar a emilio si considera que test 09 y 10 son parecidos al ultimo de token
    @Test
    public void test09loginConVariosUsuarioFuncionaYDevuelveTokensDiferentes(){
        Authentication auth = new Authentication(defaultUsers);
        Token token1 = auth.login("user1", "password1");
        Token token2 = auth.login("user2", "password2");
        assertNotEquals(token1.getToken(), token2.getToken());
    }

    @Test
    public void test10loginConMismoUsuarioDosVecesFuncionaYDevuelveTokensDiferentes(){
        Authentication auth = new Authentication(defaultUsers);
        Token token1 = auth.login("user1", "password1");
        Token token2 = auth.login("user1", "password1");
        assertNotEquals(token1.getToken(), token2.getToken());
    }

    //este es medio gede, despues ver que opina emilio
    @Test
    public void test11loginConCadaUsuarioValidoDevuelveToken(){
        Authentication auth = new Authentication(defaultUsers);

        for (String user : defaultUsers.keySet()) {
            Token token = auth.login(user, defaultUsers.get(user));
            assertNotNull(token);
        }
    }

    private void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}