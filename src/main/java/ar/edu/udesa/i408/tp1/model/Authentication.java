package ar.edu.udesa.i408.tp1.model;

import java.util.HashMap;
import java.util.Map;

public class Authentication {
    public static String userNotFoundErrorDescription = "User not found";
    public static String passwordsDoNotMatchErrorDescription = "Passwords do not match";
    public static String invalidInputErrorDescription = "Invalid input";

    private Map<String, User> users;
    private Map<String, Session> sessions = new HashMap<>();
    private Clock clock;

    public Authentication(Map<String, User> users, Clock clock) {
        this.users = Map.copyOf(users);
        this.clock = clock;
    }

    public Session login(String userId, String password) {
        assertValidInput(userId, password);
        assertValidUsername(userId);
        assertValidPassword(password, userId);

        Session session = new Session(userId, clock);
        sessions.put(userId, session);
        return session;
    }

    private void assertValidPassword(String password, String userId) {
        if (!this.users.get(userId).getPassword().equals(password)) {
            throw new RuntimeException(passwordsDoNotMatchErrorDescription);
        }
    }

    private void assertValidUsername(String userId) {
        if (!this.users.containsKey(userId)) {
            throw new RuntimeException(userNotFoundErrorDescription);
        }
    }

    private void assertValidInput(String userId, String password) {
        if (userId == null || password == null || userId.isEmpty() || password.isEmpty()) {
            throw new RuntimeException(invalidInputErrorDescription);
        }
    }
}