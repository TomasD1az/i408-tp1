package ar.edu.udesa.i408.tp1.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Token {
    private final String token;
    private final LocalDateTime creationTime;
    private static final int EXPIRATION_TIME = 300; // seconds
    private final String username;
    private boolean forcedExpired = false;
    private final Clock clock;

    public Token(String username, Clock clock) {
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.clock = clock;
        this.creationTime = clock.now();
        this.username = username;
    }

    public boolean isValid() {
        return !forcedExpired &&
                clock.now().isBefore(creationTime.plusSeconds(EXPIRATION_TIME));
    }

    public void forceExpiration() {
        this.forcedExpired = true;
    }
}