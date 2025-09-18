package ar.edu.udesa.i408.tp1;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;


public class Token {
    @Getter
    private String token;

    private LocalDateTime creationTime;
    private static int EXPIRATION_TIME = 300;

    public Token(){
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.creationTime = LocalDateTime.now();
    }

    public boolean isValid(){
        return LocalDateTime.now().isAfter(creationTime.plusSeconds(EXPIRATION_TIME));
    }
}