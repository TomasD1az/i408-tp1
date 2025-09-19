package ar.edu.udesa.i408.tp1.model;

import lombok.Getter;

@Getter
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final String password;

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
