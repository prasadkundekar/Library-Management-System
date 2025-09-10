package src.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String username;
    private String isbn;
    private String action; // Borrowed / Returned
    private String timestamp;

    public Transaction(String username, String isbn, String action) {
        this.username = username;
        this.isbn = isbn;
        this.action = action;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getUsername() {
        return username;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAction() {
        return action;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
