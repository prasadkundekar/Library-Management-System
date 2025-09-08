import java.util.ArrayList;
import java.util.Scanner;

class Book {
    private String title;
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Title: " + title + " | Author: " + author + " | ISBN: " + isbn;
    }
}

class Library {
    private ArrayList<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
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
            if (book.getTitle().equalsIgnoreCase(keyword) || book.getAuthor().equalsIgnoreCase(keyword)) {
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
            System.out.println("🗑️ Book deleted successfully!");
        } else {
            System.out.println("❌ Book not found with ISBN: " + isbn);
        }
    }
}

// Main class
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
            System.out.println("5. Exit");
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
                    System.out.println("👋 Exiting Library Management System...");
                    break;

                default:
                    System.out.println("⚠️ Invalid choice! Try again.");
            }
        } while (choice != 5);

        sc.close();
    }
}
