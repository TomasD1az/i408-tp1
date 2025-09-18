package ar.edu.udesa.i408.tp1.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
