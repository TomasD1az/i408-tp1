package org.udesa.giftcards.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.udesa.giftcards.model.Clock;
import org.udesa.giftcards.model.GiftCard;
import org.udesa.giftcards.model.Merchant;
import org.udesa.giftcards.model.User;
import org.udesa.giftcards.service.GiftCardService;
import org.udesa.giftcards.service.MerchantService;
import org.udesa.giftcards.service.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GifCardFacadeTest {

    @Autowired
    private UserService userService;
    @Autowired
    private GiftCardService giftCardService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private GifCardFacade facade;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        // Users
        userService.save(new User("Bob", "BobPass"));
        userService.save(new User("Kevin", "KevPass"));

        // Gift Cards
        giftCardService.save(new GiftCard("GC1", 10));
        giftCardService.save(new GiftCard("GC2", 5));

        // Merchants
        merchantService.save(new Merchant("M1"));
    }

    @Test
    public void userCanOpenASession() {
        assertNotNull(facade.login("Bob", "BobPass"));
    }

    @Test
    public void unkownUserCannorOpenASession() {
        assertThrows(RuntimeException.class, () -> facade.login("Stuart", "StuPass"));
    }

    @Test
    public void userCannotUseAnInvalidtoken() {
        assertThrows(RuntimeException.class, () -> facade.redeem(UUID.randomUUID(), "GC1"));
        assertThrows(RuntimeException.class, () -> facade.balance(UUID.randomUUID(), "GC1"));
        assertThrows(RuntimeException.class, () -> facade.details(UUID.randomUUID(), "GC1"));
    }

    @Test
    public void userCannotCheckOnAlienCard() {
        UUID token = facade.login("Bob", "BobPass");
        assertThrows(RuntimeException.class, () -> facade.balance(token, "GC1"));
    }

    @Test
    public void userCanRedeeemACard() {
        UUID token = facade.login("Bob", "BobPass");
        facade.redeem(token, "GC1");
        assertEquals(10, facade.balance(token, "GC1"));
    }

    @Test
    public void userCanRedeeemASecondCard() {
        UUID token = facade.login("Bob", "BobPass");
        facade.redeem(token, "GC1");
        facade.redeem(token, "GC2");
        assertEquals(10, facade.balance(token, "GC1"));
        assertEquals(5, facade.balance(token, "GC2"));
    }

    @Test
    public void multipleUsersCanRedeeemACard() {
        UUID bobsToken = facade.login("Bob", "BobPass");
        UUID kevinsToken = facade.login("Kevin", "KevPass");
        facade.redeem(bobsToken, "GC1");
        facade.redeem(kevinsToken, "GC2");
        assertEquals(10, facade.balance(bobsToken, "GC1"));
        assertEquals(5, facade.balance(kevinsToken, "GC2"));
    }

    @Test
    public void unknownMerchantCantCharge() {
        assertThrows(RuntimeException.class, () -> facade.charge("Mx", "GC1", 2, "UnCargo"));
    }

    @Test
    public void merchantCantChargeUnredeemedCard() {
        assertThrows(RuntimeException.class, () -> facade.charge("M1", "GC1", 2, "UnCargo"));
    }

    @Test
    public void merchantCanChargeARedeemedCard() {
        UUID token = facade.login("Bob", "BobPass");
        facade.redeem(token, "GC1");
        facade.charge("M1", "GC1", 2, "UnCargo");
        assertEquals(8, facade.balance(token, "GC1"));
    }

    @Test
    public void merchantCannotOverchargeACard() {
        UUID token = facade.login("Bob", "BobPass");
        facade.redeem(token, "GC1");
        assertThrows(RuntimeException.class, () -> facade.charge("M1", "GC1", 11, "UnCargo"));
    }

    @Test
    public void userCanCheckHisEmptyCharges() {
        UUID token = facade.login("Bob", "BobPass");
        facade.redeem(token, "GC1");
        assertTrue(facade.details(token, "GC1").isEmpty());
    }

    @Test
    public void userCanCheckHisCharges() {
        UUID token = facade.login("Bob", "BobPass");
        facade.redeem(token, "GC1");
        facade.charge("M1", "GC1", 2, "UnCargo");
        assertEquals("UnCargo", facade.details(token, "GC1").getLast());
    }

    @Test
    public void userCannotCheckOthersCharges() {
        facade.redeem(facade.login("Bob", "BobPass"), "GC1");
        UUID token = facade.login("Kevin", "KevPass");
        assertThrows(RuntimeException.class, () -> facade.details(token, "GC1"));
    }

    @Test
    public void tokenExpires() {
        Clock testClock = new Clock() {
            Iterator<LocalDateTime> it = List.of(LocalDateTime.now(), LocalDateTime.now().plusMinutes(16)).iterator();

            public LocalDateTime now() {
                return it.next();
            }
        };

        GifCardFacade testFacade = new GifCardFacade(testClock, userService, giftCardService, merchantService);
        UUID token = testFacade.login("Kevin", "KevPass");
        assertThrows(RuntimeException.class, () -> testFacade.redeem(token, "GC1"));
    }

}