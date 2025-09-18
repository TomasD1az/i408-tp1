package ar.edu.udesa.i408.tp1.model;


import org.junit.jupiter.api.Test;
import java.util.Map;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {
    private static final Map<String, String> defaultUser = Map.of( "user", "password");
    //private static final Map<String, String> defaultMultipleUsers = Map.of( "user1",  "password1", "user2", "password2");

    @Test
    public void test01loginConCredencialesValidasDevuelveToken(){
        Authentication auth = new Authentication(defaultUser);
        Token token = auth.login("user", "password");
        assertNotNull(token);
    }

    @Test
    public void test02loginConUsuarioQueNoExisteFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login("nouser", "password"), Authentication.USER_NOT_FOUND);
    }

    @Test
    public void test03loginConContrasenaInvalidaFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login("user", "invalidpassword"), Authentication.PASSWORDS_DO_NOT_MATCH);

    }

    @Test
    public void test04loginConUsernameConDiferenteCaseFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login("User", "password"), Authentication.USER_NOT_FOUND);
    }

    @Test
    public void test05loginConUsernameVacioFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login("", "password"), Authentication.INVALID_INPUT);
    }

    @Test
    public void test06loginConContrasenaVacioFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login("user", ""), Authentication.INVALID_INPUT);
    }

    @Test
    public void test07loginConUsuarioNullFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login(null, "password"), Authentication.INVALID_INPUT);
    }

    @Test
    public void test08loginConContrasenaNullFalla(){
        Authentication auth = new Authentication(defaultUser);
        assertThrowsLike( () -> auth.login("user", null), Authentication.INVALID_INPUT);
    }

    @Test
    public void test09loginConVariasCuentasDaTokensDistintos(){}

    @Test
    public void test10loginConMismoUsuarioDosVecesEnMenosDe5MinDevuelveTokensDiferentes(){}

    @Test
    public void test11loginConMismoUsuarioVariasVecesEnMasDe5MinutosDevuelveTokensDiferentes(){}

    @Test
    public void test12loginConCadaUsuarioDeUnMapaValidoDevuelveToken(){}
    
    private void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}
