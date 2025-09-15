import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Consumer;

// Book, Transaction, and Library classes are unchanged
// but their file I/O logic will be handled by FileManager.

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
            if (parts.length < 4) return null;
            return new Book(parts[0], parts[1], parts[2], parts[3]);
        } catch (Exception e) {
            System.err.println("Error parsing Book from CSV: " + line);
            return null;
        }
    }
}

class User {
    private String username;
    private String passwordHash;
    private String salt; // New field for security
    private String role; // Admin / User

    public User(String username, String passwordHash, String salt, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return username + "," + passwordHash + "," + salt + "," + role;
    }

    public static User fromCSV(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length < 4) return null;
            return new User(parts[0], parts[1], parts[2], parts[3]);
        } catch (Exception e) {
            System.err.println("Error parsing User from CSV: " + line);
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
            if (parts.length < 6) return null;
            String returnDate = parts[4].isEmpty() ? null : parts[4];
            long fine = Long.parseLong(parts[5]);
            return new Transaction(parts[0], parts[1], parts[2], parts[3], returnDate, fine);
        } catch (Exception e) {
            System.err.println("Error parsing Transaction from CSV: " + line);
            return null;
        }
    }
}

// --- NEW CLASS: FileManager ---
class FileManager {
    public static <T> void saveData(String filename, List<T> data, Function<T, String> toCsvFunction) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (T item : data) {
                bw.write(toCsvFunction.apply(item));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error saving to file: " + filename);
        }
    }

    public static <T> ArrayList<T> loadData(String filename, Function<String, T> fromCsvFunction) {
        ArrayList<T> data = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return data;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                T item = fromCsvFunction.apply(line);
                if (item != null) {
                    data.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error loading from file: " + filename);
        }
        return data;
    }
}
// --- END NEW CLASS ---

class Library {
    private ArrayList<Book> books;
    private ArrayList<Transaction> transactions;
    private final String BOOK_FILE = "books.csv";
    private final String TRANSACTION_FILE = "transactions.csv";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Library() {
        this.books = FileManager.loadData(BOOK_FILE, Book::fromCSV);
        this.transactions = FileManager.loadData(TRANSACTION_FILE, Transaction::fromCSV);
    }

    public void addBook(Book book) {
        if (books.stream().anyMatch(b -> b.getIsbn().equals(book.getIsbn()))) {
            System.out.println("⚠️ Book with this ISBN already exists.");
            return;
        }
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
        Book toDelete = books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
        if (toDelete != null) {
            books.remove(toDelete);
            saveBooksToFile();
            System.out.println("🗑️ Book deleted.");
        } else {
            System.out.println("❌ Book not found.");
        }
    }

    public void borrowBook(String isbn, String username) {
        Book book = books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
        if (book == null) {
            System.out.println("❌ ISBN not found.");
            return;
        }

        if (book.getStatus().equals("Available")) {
            book.setStatus("Issued");
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(7);
            Transaction t = new Transaction(username, isbn,
                    issueDate.format(formatter), dueDate.format(formatter), null, 0);
            transactions.add(t);
            saveBooksToFile();
            saveTransactionsToFile();
            System.out.println("📖 Borrowed: " + book.getTitle());
            System.out.println("📅 Due: " + dueDate.format(formatter));
        } else {
            System.out.println("⚠️ This book is not available.");
        }
    }

    public void returnBook(String isbn, String username) {
        Book book = books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
        if (book == null) {
            System.out.println("❌ ISBN not found.");
            return;
        }

        if (book.getStatus().equals("Issued")) {
            Transaction activeTransaction = transactions.stream()
                    .filter(t -> t.getIsbn().equals(isbn) && t.getUsername().equals(username) && t.getReturnDate() == null)
                    .findFirst()
                    .orElse(null);

            if (activeTransaction != null) {
                book.setStatus("Available");
                LocalDate dueDate = LocalDate.parse(activeTransaction.getDueDate(), formatter);
                LocalDate today = LocalDate.now();
                long lateDays = ChronoUnit.DAYS.between(dueDate, today);
                long fine = lateDays > 0 ? lateDays * 10 : 0;
                activeTransaction.setReturnDate(today.format(formatter));
                activeTransaction.setFine(fine);

                if (fine > 0) {
                    System.out.println("⚠️ Late by " + lateDays + " days. Fine = ₹" + fine);
                } else {
                    System.out.println("✅ Returned on time!");
                }

                saveBooksToFile();
                saveTransactionsToFile();
                System.out.println("✅ You returned: " + book.getTitle());
            } else {
                System.out.println("⚠️ This book was not issued to you, or has already been returned.");
            }
        } else {
            System.out.println("⚠️ This book was not issued.");
        }
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

    public void generateReports() {
        System.out.println("\n===== 📊 Library Reports =====");

        long totalBooks = books.size();
        System.out.println("1. Total Books: " + totalBooks);

        long available = books.stream().filter(b -> b.getStatus().equals("Available")).count();
        long issued = totalBooks - available;
        System.out.println("2. Available Books: " + available);
        System.out.println("3. Issued Books: " + issued);

        Map<String, Long> borrowCount = new HashMap<>();
        for (Transaction t : transactions) {
            borrowCount.put(t.getIsbn(), borrowCount.getOrDefault(t.getIsbn(), 0L) + 1);
        }
        if (!borrowCount.isEmpty()) {
            String topIsbn = Collections.max(borrowCount.entrySet(), Map.Entry.comparingByValue()).getKey();
            long times = borrowCount.get(topIsbn);
            Book topBook = books.stream().filter(b -> b.getIsbn().equals(topIsbn)).findFirst().orElse(null);
            if (topBook != null) System.out.println("4. Most Borrowed Book: " + topBook.getTitle() + " (" + times + " times)");
        } else System.out.println("4. Most Borrowed Book: None yet.");

        Map<String, Long> fineMap = new HashMap<>();
        for (Transaction t : transactions) {
            fineMap.put(t.getUsername(), fineMap.getOrDefault(t.getUsername(), 0L) + t.getFine());
        }
        if (!fineMap.isEmpty()) {
            String topUser = Collections.max(fineMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            long maxFine = fineMap.get(topUser);
            System.out.println("5. User with Highest Fine: " + topUser + " (₹" + maxFine + ")");
        } else System.out.println("5. User with Highest Fine: None yet.");

        System.out.println("==============================");
    }

    private void saveBooksToFile() {
        FileManager.saveData(BOOK_FILE, books, Book::toCSV);
    }

    private void saveTransactionsToFile() {
        FileManager.saveData(TRANSACTION_FILE, transactions, Transaction::toCSV);
    }
}

class UserManager {
    private ArrayList<User> users;
    private final String USER_FILE = "users.csv";

    public UserManager() {
        this.users = FileManager.loadData(USER_FILE, User::fromCSV);
        if (users.isEmpty()) {
            addUser("admin", "admin123", "Admin");
            System.out.println("⚠️ Default Admin created (username: admin, password: admin123)");
        }
    }

    public void addUser(String username, String password, String role) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        users.add(new User(username, hash, salt, role));
        saveUsersToFile();
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                String hash = hashPassword(password, u.getSalt());
                if (u.getPasswordHash().equals(hash)) {
                    return u;
                }
            }
        }
        return null;
    }

    public boolean userExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    private void saveUsersToFile() {
        FileManager.saveData(USER_FILE, users, User::toString);
    }

    // --- New Methods ---
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String pwd, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] bytes = md.digest(pwd.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash error.", e);
        }
    }
    // --- End New Methods ---
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

            try {
                int opt = sc.nextInt();
                sc.nextLine(); // Consume newline

                if (opt == 1) {
                    System.out.print("👤 Username: ");
                    String u = sc.nextLine();
                    System.out.print("🔑 Password: ");
                    String p = sc.nextLine();
                    loggedInUser = userManager.login(u, p);
                    if (loggedInUser == null) System.out.println("❌ Invalid username or password!");
                    else System.out.println("✅ Welcome " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
                } else if (opt == 2) {
                    System.out.print("👤 New Username: ");
                    String u = sc.nextLine();
                    if (userManager.userExists(u)) { System.out.println("⚠️ Username exists!"); continue; }
                    System.out.print("🔑 Password: ");
                    String p1 = sc.nextLine();
                    System.out.print("🔑 Confirm Password: ");
                    String p2 = sc.nextLine();
                    if (!p1.equals(p2)) { System.out.println("❌ Passwords mismatch!"); continue; }
                    userManager.addUser(u, p1, "User");
                    System.out.println("✅ Registration successful!");
                } else if (opt == 3) { System.out.println("👋 Bye!"); return; }
                else System.out.println("⚠️ Invalid choice!");
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                sc.nextLine(); // Clear the invalid input from the scanner
            }
        }

        // Simplified menu logic
        int ch;
        do {
            System.out.println("\n===== Menu =====");
            if (loggedInUser.getRole().equalsIgnoreCase("Admin")) {
                System.out.println("1. Add Book");
                System.out.println("2. Delete Book");
                System.out.println("3. Generate Reports");
            }
            System.out.println("4. Display All Books");
            System.out.println("5. Search Book");
            System.out.println("6. Borrow Book");
            System.out.println("7. Return Book");
            System.out.println("8. View My History");
            System.out.println("9. Exit");

            System.out.print("👉 Choice: ");
            try {
                ch = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (ch) {
                    case 1 -> {
                        if (!loggedInUser.getRole().equalsIgnoreCase("Admin")) { System.out.println("❌ Access denied."); break; }
                        System.out.print("Title: "); String t = sc.nextLine();
                        System.out.print("Author: "); String a = sc.nextLine();
                        System.out.print("ISBN: "); String i = sc.nextLine();
                        library.addBook(new Book(t,a,i));
                    }
                    case 2 -> {
                        if (!loggedInUser.getRole().equalsIgnoreCase("Admin")) { System.out.println("❌ Access denied."); break; }
                        System.out.print("ISBN: "); library.deleteBook(sc.nextLine());
                    }
                    case 3 -> {
                        if (!loggedInUser.getRole().equalsIgnoreCase("Admin")) { System.out.println("❌ Access denied."); break; }
                        library.generateReports();
                    }
                    case 4 -> library.displayBooks();
                    case 5 -> { System.out.print("Keyword: "); library.searchBook(sc.nextLine()); }
                    case 6 -> { System.out.print("ISBN: "); library.borrowBook(sc.nextLine(), loggedInUser.getUsername()); }
                    case 7 -> { System.out.print("ISBN: "); library.returnBook(sc.nextLine(), loggedInUser.getUsername()); }
                    case 8 -> library.viewUserHistory(loggedInUser.getUsername());
                    case 9 -> System.out.println("👋 Bye!");
                    default -> System.out.println("⚠️ Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                ch = 0; // Reset choice to loop again
                sc.nextLine(); // Clear the invalid input
            }
        } while (ch != 9);

        sc.close();
    }
}