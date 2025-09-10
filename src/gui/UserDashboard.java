package gui;

import model.Book;
import model.User;
import service.Library;
import service.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UserDashboard extends JFrame {
    private Library library;
    private User user;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;

    public UserDashboard(Library lib, UserManager um, User user) {
        this.library = lib;
        this.user = user;

        setTitle("User Dashboard - " + user.getUsername());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        bookTableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN", "Status"},0);
        bookTable = new JTable(bookTableModel);
        refreshBookTable();

        JPanel btnPanel = new JPanel();
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        JButton historyBtn = new JButton("View History");
        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(historyBtn);

        borrowBtn.addActionListener(e -> borrowBook());
        returnBtn.addActionListener(e -> returnBook());
        historyBtn.addActionListener(e -> viewHistory());

        add(new JScrollPane(bookTable), "Center");
        add(btnPanel, "South");

        setVisible(true);
    }

    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        List<Book> books = library.getAllBooks();
        for (Book b : books) bookTableModel.addRow(new Object[]{b.getTitle(), b.getAuthor(), b.getIsbn(), b.getStatus()});
    }

    private void borrowBook() {
        int row = bookTable.getSelectedRow();
        if (row >= 0) {
            String isbn = (String) bookTable.getValueAt(row, 2);
            library.borrowBook(isbn, user.getUsername());
            refreshBookTable();
        } else JOptionPane.showMessageDialog(this, "Select a book to borrow.");
    }

    private void returnBook() {
        int row = bookTable.getSelectedRow();
        if (row >= 0) {
            String isbn = (String) bookTable.getValueAt(row, 2);
            library.returnBook(isbn, user.getUsername());
            refreshBookTable();
        } else JOptionPane.showMessageDialog(this, "Select a book to return.");
    }

    private void viewHistory() {
        library.viewUserHistoryGUI(user.getUsername());
    }
}
