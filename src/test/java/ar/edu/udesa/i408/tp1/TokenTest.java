package ar.edu.udesa.i408.tp1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenTest {

    @Test
    public void test01tokenGeneradoEsValidoAlCrearse(){
        Token token = new Token();
        assertNotNull(token.getToken());
    }

    @Test
    public void test02tokenGeneradoTiene32Caracteres(){
        Token token = new Token();
        assertEquals(32, token.getToken().length());
    }

    @Test
    public void test03tokenExpiraDespuesDe5Minutos(){}

    @Test
    public void test04tokenGeneradosSonUnicos(){}
}
