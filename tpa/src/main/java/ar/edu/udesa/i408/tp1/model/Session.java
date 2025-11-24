package ar.edu.udesa.i408.tp1.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Session {
    private String token;
    private LocalDateTime creationTime;
    private int EXPIRATION_TIME = 300;
    private String userId;
    private Clock clock;

    public Session(String userId, Clock clock) {
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.clock = clock;
        this.creationTime = clock.now();
        this.userId = userId;
    }

    public void update(){
        this.creationTime = clock.now();
    }

    public boolean isValid() {
        return clock.now().isBefore(creationTime.plusSeconds(EXPIRATION_TIME));
    }
}