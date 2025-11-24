package org.udesa.giftcards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.udesa.giftcards.facade.GifCardFacade;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/giftcards")
public class GiftCardController {

    private final GifCardFacade facade;

    @Autowired
    public GiftCardController(GifCardFacade facade) {
        this.facade = facade;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleIllegalArgument(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String user,
            @RequestParam String pass) {

        UUID token = facade.login(user, pass);
        return ResponseEntity.ok(Map.of("token", token.toString()));
    }

    @PostMapping("/{cardId}/redeem")
    public ResponseEntity<String> redeemCard(@RequestHeader("Authorization") String header,
            @PathVariable String cardId) {

        UUID token = extractToken(header);
        facade.redeem(token, cardId);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<Map<String, Object>> balance(@RequestHeader("Authorization") String header,
            @PathVariable String cardId) {

        UUID token = extractToken(header);
        int balance = facade.balance(token, cardId);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/{cardId}/details")
    public ResponseEntity<Map<String, Object>> details(@RequestHeader("Authorization") String tokenHeader,
            @PathVariable String cardId) {

        UUID token = extractToken(tokenHeader);
        List<String> details = facade.details(token, cardId);
        return ResponseEntity.ok(Map.of("details", details));
    }

    @PostMapping("/{cardId}/charge")
    public ResponseEntity<String> charge(@RequestParam String merchant,
            @RequestParam int amount,
            @RequestParam String description,
            @PathVariable String cardId) {

        facade.charge(merchant, cardId, amount, description);
        return ResponseEntity.ok("OK");
    }

    private UUID extractToken(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new RuntimeException("InvalidToken");

        return UUID.fromString(header.substring(7));
    }
}