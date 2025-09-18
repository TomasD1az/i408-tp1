package ar.edu.udesa.i408.tp1.model;

import java.util.Map;

public class Authentication {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
    public static final String INVALID_INPUT = "Invalid input";
    private Map<String, String> users;

    public Authentication(Map<String, String> users) {
        this.users = Map.copyOf(users);
    }

    public Token login(String username, String password) {
        assertValidInput(username, password);
        assertIsValidUser(username);
        assertIfPasswordsMatch(username, password);
        return new Token(username); // 👈 now linked to user
    }

    public void assertValidInput(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) throw new RuntimeException(INVALID_INPUT);
    }

    public void assertIsValidUser(String username) {
        if(!users.containsKey(username)) throw new RuntimeException(USER_NOT_FOUND);
    }

    public void assertIfPasswordsMatch(String username, String password) {
        if(!users.get(username).equals(password)) throw new RuntimeException(PASSWORDS_DO_NOT_MATCH);
    }
}