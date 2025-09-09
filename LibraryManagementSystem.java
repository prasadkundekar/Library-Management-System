import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Book {
    private String title;
    private String author;
    private String isbn;
    private String status;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = "Available";
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
        return "Title: " + title + " | Author: " + author + " | ISBN: " + isbn + " | Status: " + status;
    }

    public String toFileString() {
        return toString();
    }

    public static Book fromFileString(String line) {
        try {
            String[] parts = line.split("\\|");
            String title = parts[0].split("Title:")[1].trim();
            String author = parts[1].split("Author:")[1].trim();
            String isbn = parts[2].split("ISBN:")[1].trim();
            String status = parts[3].split("Status:")[1].trim();
            return new Book(title, author, isbn, status);
        } catch (Exception e) {
            return null;
        }
    }
}

class Library {
    private ArrayList<Book> books = new ArrayList<>();
    private final String FILE_NAME = "books.txt";

    public Library() {
        loadBooksFromFile();
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooksToFile();
        System.out.println("✅ Book added successfully!");
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("📚 No books available in the library.");
            return;
        }
        System.out.println("\n--- Library Books ---");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void searchBook(String keyword) {
        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("🔎 Found: " + book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("❌ No book found with the given keyword.");
        }
    }

    public void deleteBook(String isbn) {
        Book toDelete = null;
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                toDelete = book;
                break;
            }
        }
        if (toDelete != null) {
            books.remove(toDelete);
            saveBooksToFile();
            System.out.println("🗑️ Book deleted successfully!");
        } else {
            System.out.println("❌ Book not found with ISBN: " + isbn);
        }
    }

    // Borrow book
    public void borrowBook(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                if (book.getStatus().equals("Available")) {
                    book.setStatus("Issued");
                    saveBooksToFile();
                    System.out.println("📖 You borrowed: " + book.getTitle());
                    return;
                } else {
                    System.out.println("⚠️ Book is already issued.");
                    return;
                }
            }
        }
        System.out.println("❌ Book not found with ISBN: " + isbn);
    }

    // Return book
    public void returnBook(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                if (book.getStatus().equals("Issued")) {
                    book.setStatus("Available");
                    saveBooksToFile();
                    System.out.println("✅ You returned: " + book.getTitle());
                    return;
                } else {
                    System.out.println("⚠️ This book was not issued.");
                    return;
                }
            }
        }
        System.out.println("❌ Book not found with ISBN: " + isbn);
    }

    public void showStatistics() {
        int total = books.size();
        int available = 0;
        int issued = 0;

        for (Book book : books) {
            if (book.getStatus().equalsIgnoreCase("Available")) {
                available++;
            } else if (book.getStatus().equalsIgnoreCase("Issued")) {
                issued++;
            }
        }

        System.out.println("\n--- 📊 Library Statistics ---");
        System.out.println("Total Books   : " + total);
        System.out.println("Available     : " + available);
        System.out.println("Issued        : " + issued);
    }

    private void saveBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                writer.write(book.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error saving books: " + e.getMessage());
        }
    }

    private void loadBooksFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Book book = Book.fromFileString(line);
                if (book != null) {
                    books.add(book);
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error loading books: " + e.getMessage());
        }
    }
}

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();
        int choice;

        do {
            System.out.println("\n===== Library Book Management =====");
            System.out.println("1. Add Book");
            System.out.println("2. Display Books");
            System.out.println("3. Search Book (by Title/Author)");
            System.out.println("4. Delete Book (by ISBN)");
            System.out.println("5. Borrow Book (by ISBN)");
            System.out.println("6. Return Book (by ISBN)");
            System.out.println("7. Show Statistics");
            System.out.println("8. Exit");
            System.out.print("👉 Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Book Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Book Author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter Book ISBN: ");
                    String isbn = sc.nextLine();
                    library.addBook(new Book(title, author, isbn));
                    break;

                case 2:
                    library.displayBooks();
                    break;

                case 3:
                    System.out.print("Enter Title/Author to Search: ");
                    String keyword = sc.nextLine();
                    library.searchBook(keyword);
                    break;

                case 4:
                    System.out.print("Enter ISBN of the book to delete: ");
                    String delIsbn = sc.nextLine();
                    library.deleteBook(delIsbn);
                    break;

                case 5:
                    System.out.print("Enter ISBN of the book to borrow: ");
                    String borrowIsbn = sc.nextLine();
                    library.borrowBook(borrowIsbn);
                    break;

                case 6:
                    System.out.print("Enter ISBN of the book to return: ");
                    String returnIsbn = sc.nextLine();
                    library.returnBook(returnIsbn);
                    break;

                case 7:
                    library.showStatistics();
                    break;

                case 8:
                    System.out.println("👋 Exiting Library Management System...");
                    break;

                default:
                    System.out.println("⚠️ Invalid choice! Try again.");
            }
        } while (choice != 8);

        sc.close();
    }
}
