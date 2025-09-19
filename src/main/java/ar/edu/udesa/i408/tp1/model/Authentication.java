package ar.edu.udesa.i408.tp1.model;

import java.util.Map;


public class Authentication {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
    public static final String INVALID_INPUT = "Invalid input";

    private final Map<String, User> users;

    public Authentication(Map<String, User> users) {
        this.users = Map.copyOf(users);
    }

    public Token login(String username, String password) {
        assertValidInput(username, password);
        User user = users.get(username);
        if (user == null) {
            throw new RuntimeException(USER_NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException(PASSWORDS_DO_NOT_MATCH);
        }
        return new Token(username);
    }

    private void assertValidInput(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new RuntimeException(INVALID_INPUT);
        }
    }
}