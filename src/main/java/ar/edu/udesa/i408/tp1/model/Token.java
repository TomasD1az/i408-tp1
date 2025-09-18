package ar.edu.udesa.i408.tp1.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Token {
    private String token;
    private LocalDateTime creationTime;
    private static final int EXPIRATION_TIME = 300;
    private String username;

    public Token(String username) {
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.creationTime = LocalDateTime.now();
        this.username = username;
    }

    public boolean isValid() { return LocalDateTime.now().isBefore(creationTime.plusSeconds(EXPIRATION_TIME));}
}