import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

class Book {
    private String title;
    private String author;
    private String isbn;
    private String status;

    public Book(String title, String author, String isbn) {
        this(title, author, isbn, "Available");
    }

    public Book(String title, String author, String isbn, String status) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return title + " | " + author + " | ISBN: " + isbn + " | " + status;
    }

    public String toCSV() {
        return title + "," + author + "," + isbn + "," + status;
    }

    public static Book fromCSV(String line) {
        try {
            String[] parts = line.split(",");
            return new Book(parts[0], parts[1], parts[2], parts[3]);
        } catch (Exception e) {
            return null;
        }
    }
}

class User {
    private String username;
    private String passwordHash;
    private String role; // Admin / User

    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return username + "," + passwordHash + "," + role;
    }

    public static User fromCSV(String line) {
        try {
            String[] parts = line.split(",");
            return new User(parts[0], parts[1], parts[2]);
        } catch (Exception e) {
            return null;
        }
    }
}

class Transaction {
    private String username;
    private String isbn;
    private String issueDate;
    private String dueDate;
    private String returnDate;
    private long fine;

    public Transaction(String username, String isbn, String issueDate, String dueDate, String returnDate, long fine) {
        this.username = username;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public String getUsername() { return username; }
    public String getIsbn() { return isbn; }
    public String getIssueDate() { return issueDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public long getFine() { return fine; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setFine(long fine) { this.fine = fine; }

    @Override
    public String toString() {
        return "User: " + username + " | ISBN: " + isbn + " | Issued: " + issueDate +
                " | Due: " + dueDate + " | Returned: " + (returnDate == null ? "Not Returned" : returnDate) +
                " | Fine: ₹" + fine;
    }

    public String toCSV() {
        return username + "," + isbn + "," + issueDate + "," + dueDate + "," +
                (returnDate == null ? "" : returnDate) + "," + fine;
    }

    public static Transaction fromCSV(String line) {
        try {
            String[] parts = line.split(",");
            String returnDate = parts[4].isEmpty() ? null : parts[4];
            long fine = Long.parseLong(parts[5]);
            return new Transaction(parts[0], parts[1], parts[2], parts[3], returnDate, fine);
        } catch (Exception e) {
            return null;
        }
    }
}

class Library {
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private final String BOOK_FILE = "books.csv";
    private final String TRANSACTION_FILE = "transactions.csv";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Library() {
        loadBooksFromFile();
        loadTransactionsFromFile();
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooksToFile();
        System.out.println("✅ Book added successfully!");
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("📚 No books available.");
            return;
        }
        System.out.println("\n--- Library Books ---");
        for (Book b : books) System.out.println(b);
    }

    public void searchBook(String keyword) {
        boolean found = false;
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    b.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                    b.getIsbn().equals(keyword)) {
                System.out.println("🔎 Found: " + b);
                found = true;
            }
        }
        if (!found) System.out.println("❌ No book found.");
    }

    public void deleteBook(String isbn) {
        Book toDelete = null;
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) { toDelete = b; break; }
        }
        if (toDelete != null) {
            books.remove(toDelete);
            saveBooksToFile();
            System.out.println("🗑️ Book deleted.");
        } else {
            System.out.println("❌ Book not found.");
        }
    }

    public void borrowBook(String isbn, String username) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (b.getStatus().equals("Available")) {
                    b.setStatus("Issued");
                    LocalDate issueDate = LocalDate.now();
                    LocalDate dueDate = issueDate.plusDays(7);
                    Transaction t = new Transaction(username, isbn,
                            issueDate.format(formatter), dueDate.format(formatter), null, 0);
                    transactions.add(t);
                    saveBooksToFile();
                    saveTransactionsToFile();
                    System.out.println("📖 Borrowed: " + b.getTitle());
                    System.out.println("📅 Due: " + dueDate.format(formatter));
                    return;
                } else {
                    System.out.println("⚠️ Already issued.");
                    return;
                }
            }
        }
        System.out.println("❌ ISBN not found.");
    }

    public void returnBook(String isbn, String username) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (b.getStatus().equals("Issued")) {
                    b.setStatus("Available");
                    for (Transaction t : transactions) {
                        if (t.getIsbn().equals(isbn) && t.getUsername().equals(username) && t.getReturnDate() == null) {
                            LocalDate dueDate = LocalDate.parse(t.getDueDate(), formatter);
                            LocalDate today = LocalDate.now();
                            long lateDays = ChronoUnit.DAYS.between(dueDate, today);
                            long fine = lateDays > 0 ? lateDays * 10 : 0;
                            t.setReturnDate(today.format(formatter));
                            t.setFine(fine);
                            if (fine > 0) {
                                System.out.println("⚠️ Late by " + lateDays + " days. Fine = ₹" + fine);
                            } else {
                                System.out.println("✅ Returned on time!");
                            }
                            break;
                        }
                    }
                    saveBooksToFile();
                    saveTransactionsToFile();
                    System.out.println("✅ You returned: " + b.getTitle());
                    return;
                } else {
                    System.out.println("⚠️ This book was not issued.");
                    return;
                }
            }
        }
        System.out.println("❌ ISBN not found.");
    }

    public void viewUserHistory(String username) {
        System.out.println("\n--- Borrowing History of " + username + " ---");
        boolean found = false;
        for (Transaction t : transactions) {
            if (t.getUsername().equals(username)) {
                System.out.println(t);
                found = true;
            }
        }
        if (!found) System.out.println("📭 No records found.");
    }

    private void saveBooksToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOK_FILE))) {
            for (Book b : books) {
                bw.write(b.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error saving books.");
        }
    }

    private void loadBooksFromFile() {
        File f = new File(BOOK_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Book b = Book.fromCSV(line);
                if (b != null) books.add(b);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error loading books.");
        }
    }

    private void saveTransactionsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTION_FILE))) {
            for (Transaction t : transactions) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error saving transactions.");
        }
    }

    private void loadTransactionsFromFile() {
        File f = new File(TRANSACTION_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Transaction t = Transaction.fromCSV(line);
                if (t != null) transactions.add(t);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error loading transactions.");
        }
    }
}

class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private final String USER_FILE = "users.csv";

    public UserManager() {
        loadUsersFromFile();
        if (users.isEmpty()) {
            addUser("admin", "admin123", "Admin");
            System.out.println("⚠️ Default Admin created (username: admin, password: admin123)");
        }
    }

    public void addUser(String username, String password, String role) {
        String hash = hashPassword(password);
        users.add(new User(username, hash, role));
        saveUsersToFile();
    }

    public User login(String username, String password) {
        String hash = hashPassword(password);
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPasswordHash().equals(hash)) return u;
        }
        return null;
    }

    public boolean userExists(String username) {
        for (User u : users) if (u.getUsername().equalsIgnoreCase(username)) return true;
        return false;
    }

    private void loadUsersFromFile() {
        File f = new File(USER_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = User.fromCSV(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error loading users.");
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User u : users) {
                bw.write(u.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error saving users.");
        }
    }

    public static String hashPassword(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(pwd.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash error.");
        }
    }
}

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();
        UserManager userManager = new UserManager();

        User loggedInUser = null;

        while (loggedInUser == null) {
            System.out.println("\n===== Welcome to Library =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("👉 Choice: ");
            int opt = sc.nextInt(); sc.nextLine();

            if (opt == 1) {
                System.out.print("👤 Username: ");
                String u = sc.nextLine();
                System.out.print("🔑 Password: ");
                String p = sc.nextLine();
                loggedInUser = userManager.login(u, p);
                if (loggedInUser == null) System.out.println("❌ Invalid login!");
                else System.out.println("✅ Welcome " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
            } else if (opt == 2) {
                System.out.print("👤 New Username: ");
                String u = sc.nextLine();
                if (userManager.userExists(u)) {
                    System.out.println("⚠️ Username exists!");
                    continue;
                }
                System.out.print("🔑 Password: ");
                String p1 = sc.nextLine();
                System.out.print("🔑 Confirm Password: ");
                String p2 = sc.nextLine();
                if (!p1.equals(p2)) { System.out.println("❌ Passwords mismatch!"); continue; }
                userManager.addUser(u, p1, "User");
                System.out.println("✅ Registration successful!");
            } else if (opt == 3) {
                System.out.println("👋 Bye!");
                return;
            } else System.out.println("⚠️ Invalid choice!");
        }

        int ch;
        do {
            System.out.println("\n===== Menu =====");
            if (loggedInUser.getRole().equalsIgnoreCase("Admin")) {
                System.out.println("1. Add Book");
                System.out.println("2. Display Books");
                System.out.println("3. Search Book");
                System.out.println("4. Delete Book");
                System.out.println("5. Borrow Book");
                System.out.println("6. Return Book");
                System.out.println("7. View My History");
                System.out.println("8. Exit");
            } else {
                System.out.println("1. Display Books");
                System.out.println("2. Search Book");
                System.out.println("3. Borrow Book");
                System.out.println("4. Return Book");
                System.out.println("5. View My History");
                System.out.println("6. Exit");
            }

            System.out.print("👉 Choice: ");
            ch = sc.nextInt(); sc.nextLine();

            if (loggedInUser.getRole().equalsIgnoreCase("Admin")) {
                switch (ch) {
                    case 1 -> {
                        System.out.print("Title: "); String t = sc.nextLine();
                        System.out.print("Author: "); String a = sc.nextLine();
                        System.out.print("ISBN: "); String i = sc.nextLine();
                        library.addBook(new Book(t,a,i));
                    }
                    case 2 -> library.displayBooks();
                    case 3 -> { System.out.print("Keyword: "); library.searchBook(sc.nextLine()); }
                    case 4 -> { System.out.print("ISBN: "); library.deleteBook(sc.nextLine()); }
                    case 5 -> { System.out.print("ISBN: "); library.borrowBook(sc.nextLine(), loggedInUser.getUsername()); }
                    case 6 -> { System.out.print("ISBN: "); library.returnBook(sc.nextLine(), loggedInUser.getUsername()); }
                    case 7 -> library.viewUserHistory(loggedInUser.getUsername());
                    case 8 -> System.out.println("👋 Bye!");
                    default -> System.out.println("⚠️ Invalid!");
                }
            } else {
                switch (ch) {
                    case 1 -> library.displayBooks();
                    case 2 -> { System.out.print("Keyword: "); library.searchBook(sc.nextLine()); }
                    case 3 -> { System.out.print("ISBN: "); library.borrowBook(sc.nextLine(), loggedInUser.getUsername()); }
                    case 4 -> { System.out.print("ISBN: "); library.returnBook(sc.nextLine(), loggedInUser.getUsername()); }
                    case 5 -> library.viewUserHistory(loggedInUser.getUsername());
                    case 6 -> System.out.println("👋 Bye!");
                    default -> System.out.println("⚠️ Invalid!");
                }
            }
        } while ((loggedInUser.getRole().equalsIgnoreCase("Admin") && ch != 8) ||
                (loggedInUser.getRole().equalsIgnoreCase("User") && ch != 6));

        sc.close();
    }
}
