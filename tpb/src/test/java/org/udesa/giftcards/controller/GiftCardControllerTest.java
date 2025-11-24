package org.udesa.giftcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.giftcards.facade.GifCardFacade;

import java.util.Map;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GiftCardController.class)
public class GiftCardControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private GifCardFacade facade;


    @Test
    public void test01LoginReturnsTokenWhenValidCredentials() throws Exception {
        UUID token = UUID.randomUUID();
        when(facade.login("Bob", "Pass")).thenReturn(token);

        var result = mvc.perform(post("/api/giftcards/login")
                        .param("user", "Bob")
                        .param("pass", "Pass"))
                .andExpect(status().isOk())
                .andReturn();

        var json = jsonOf(result.getResponse().getContentAsString());
        assertEquals(token.toString(), json.get("token"));
    }

    @Test
    public void test02LoginFailsWhenFacadeThrows() throws Exception {
        when(facade.login("Bad", "X"))
                .thenThrow(new RuntimeException("InvalidUser"));

        mvc.perform(post("/api/giftcards/login")
                        .param("user", "Bad")
                        .param("pass", "X"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("InvalidUser"));
    }


    @Test
    public void test03RedeemWorksWithValidToken() throws Exception {
        UUID token = UUID.randomUUID();
        doNothing().when(facade).redeem(token, "GC1");

        mvc.perform(post("/api/giftcards/GC1/redeem")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void test04RedeemFailsWhenFacadeThrows() throws Exception {
        UUID token = UUID.randomUUID();
        doThrow(new RuntimeException("InvalidToken"))
                .when(facade).redeem(token, "GC1");

        mvc.perform(post("/api/giftcards/GC1/redeem")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("InvalidToken"));
    }

    @Test
    public void test05RedeemFailsWithMalformedHeader() throws Exception {
        mvc.perform(post("/api/giftcards/GC1/redeem")
                        .header("Authorization", "Whatever"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("InvalidToken"));
    }


    @Test
    public void test06BalanceReturnsValue() throws Exception {
        UUID token = UUID.randomUUID();
        when(facade.balance(token, "GC1")).thenReturn(50);

        var result = mvc.perform(get("/api/giftcards/GC1/balance")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andReturn();

        var json = jsonOf(result.getResponse().getContentAsString());
        assertEquals(50, json.get("balance"));
    }

    @Test
    public void test07BalanceFailsWhenFacadeThrows() throws Exception {
        UUID token = UUID.randomUUID();
        when(facade.balance(token, "GC1"))
                .thenThrow(new RuntimeException("InvalidToken"));

        mvc.perform(get("/api/giftcards/GC1/balance")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("InvalidToken"));
    }


    @Test
    public void test08DetailsReturnsList() throws Exception {
        UUID token = UUID.randomUUID();
        when(facade.details(token, "GC1"))
                .thenReturn(List.of("A", "B"));

        var result = mvc.perform(get("/api/giftcards/GC1/details")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andReturn();

        var json = jsonOf(result.getResponse().getContentAsString());
        assertEquals(List.of("A", "B"), json.get("details"));
    }

    @Test
    public void test09DetailsFailsWithInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();
        when(facade.details(token, "GC1"))
                .thenThrow(new RuntimeException("InvalidToken"));

        mvc.perform(get("/api/giftcards/GC1/details")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("InvalidToken"));
    }


    @Test
    public void test10ChargeSucceeds() throws Exception {
        doNothing().when(facade).charge("M1", "GC1", 3, "Desc");

        mvc.perform(post("/api/giftcards/GC1/charge")
                        .param("merchant", "M1")
                        .param("amount", "3")
                        .param("description", "Desc"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void test11ChargeFailsWhenMerchantInvalid() throws Exception {
        doThrow(new RuntimeException("InvalidMerchant"))
                .when(facade).charge("BAD", "GC1", 10, "X");

        mvc.perform(post("/api/giftcards/GC1/charge")
                        .param("merchant", "BAD")
                        .param("amount", "10")
                        .param("description", "X"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("InvalidMerchant"));
    }


    private Map<String, Object> jsonOf(String body) throws Exception {
        return mapper.readValue(body, Map.class);
    }

    private String bearer(UUID token) {
        return "Bearer " + token;
    }
}