package src.service;

import model.Book;
import model.Transaction;

import javax.swing.*;
import java.util.*;

public class Library {
    private List<Book> books = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public void deleteBook(String isbn) {
        books.removeIf(b -> b.getIsbn().equals(isbn));
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public void borrowBook(String isbn, String username) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn) && b.getStatus().equalsIgnoreCase("Available")) {
                b.setStatus("Issued");
                transactions.add(new Transaction(username, isbn, "Borrowed"));
                JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Book not available!");
    }

    public void returnBook(String isbn, String username) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn) && b.getStatus().equalsIgnoreCase("Issued")) {
                b.setStatus("Available");
                transactions.add(new Transaction(username, isbn, "Returned"));
                JOptionPane.showMessageDialog(null, "Book returned successfully!");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Invalid return attempt!");
    }

    public void generateReportsGUI() {
        StringBuilder report = new StringBuilder();
        report.append("📊 Library Report\n\n");
        report.append("Total books: ").append(books.size()).append("\n");
        long issued = books.stream().filter(b -> b.getStatus().equals("Issued")).count();
        report.append("Issued books: ").append(issued).append("\n");
        report.append("Available books: ").append(books.size() - issued).append("\n");

        JOptionPane.showMessageDialog(null, report.toString(), "Library Reports", JOptionPane.INFORMATION_MESSAGE);
    }
}
