package ar.edu.udesa.i408.tp1.model;

import java.util.Map;

public class Authentication {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";
    public static final String INVALID_INPUT = "Invalid input";

    private final Map<String, User> users;
    private final Clock clock;

    public Authentication(Map<String, User> users, Clock clock) {
        this.users = Map.copyOf(users);
        this.clock = clock;
    }

    public Session login(String userId, String password) {
        assertValidInput(userId, password);
        User user = users.get(userId); // Now looking up by userId, not username
        if (user == null) {
            throw new RuntimeException(USER_NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException(PASSWORDS_DO_NOT_MATCH);
        }
        return new Session(userId, clock); // Pass userId to token
    }

    private void assertValidInput(String userId, String password) {
        if (userId == null || password == null || userId.isEmpty() || password.isEmpty()) {
            throw new RuntimeException(INVALID_INPUT);
        }
    }
}