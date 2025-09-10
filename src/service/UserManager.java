package src.service;

import model.User;
import java.util.*;

public class UserManager {
    private Map<String, User> users = new HashMap<>();

    public UserManager() {
        // Default admin
        users.put("admin", new User("admin", "admin", "Admin"));
    }

    public void addUser(String username, String password, String role) {
        users.put(username, new User(username, password, role));
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
