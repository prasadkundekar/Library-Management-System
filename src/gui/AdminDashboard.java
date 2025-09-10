package src.gui;

import model.Book;
import model.User;
import service.Library;
import service.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private Library library;
    private UserManager userManager;
    private User admin;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;

    public AdminDashboard(Library lib, UserManager um, User admin) {
        this.library = lib;
        this.userManager = um;
        this.admin = admin;

        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Books Tab ---
        JPanel booksPanel = new JPanel(new BorderLayout());
        bookTableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN", "Status"},0);
        bookTable = new JTable(bookTableModel);
        refreshBookTable();
        booksPanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel bookBtnPanel = new JPanel();
        JButton addBookBtn = new JButton("Add Book");
        JButton deleteBookBtn = new JButton("Delete Book");
        bookBtnPanel.add(addBookBtn);
        bookBtnPanel.add(deleteBookBtn);
        booksPanel.add(bookBtnPanel, BorderLayout.SOUTH);

        addBookBtn.addActionListener(e -> addBook());
        deleteBookBtn.addActionListener(e -> deleteBook());

        tabbedPane.add("Books", booksPanel);

        // --- Borrow/Return Tab ---
        JPanel borrowPanel = new JPanel();
        JButton borrowBtn = new JButton("Borrow Book");
        JButton returnBtn = new JButton("Return Book");
        borrowPanel.add(borrowBtn);
        borrowPanel.add(returnBtn);
        borrowBtn.addActionListener(e -> borrowBook());
        returnBtn.addActionListener(e -> returnBook());
        tabbedPane.add("Borrow/Return", borrowPanel);

        // --- Reports Tab ---
        JPanel reportPanel = new JPanel();
        JButton reportBtn = new JButton("Generate Report");
        reportPanel.add(reportBtn);
        reportBtn.addActionListener(e -> generateReports());
        tabbedPane.add("Reports", reportPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        List<Book> books = library.getAllBooks();
        for (Book b : books) bookTableModel.addRow(new Object[]{b.getTitle(), b.getAuthor(), b.getIsbn(), b.getStatus()});
    }

    private void addBook() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField isbn = new JTextField();
        Object[] msg = {"Title:", title, "Author:", author, "ISBN:", isbn};
        int result = JOptionPane.showConfirmDialog(this, msg, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            library.addBook(new Book(title.getText(), author.getText(), isbn.getText()));
            refreshBookTable();
        }
    }

    private void deleteBook() {
        int row = bookTable.getSelectedRow();
        if (row >= 0) {
            String isbn = (String) bookTable.getValueAt(row, 2);
            library.deleteBook(isbn);
            refreshBookTable();
        } else JOptionPane.showMessageDialog(this, "Select a book to delete.");
    }

    private void borrowBook() {
        String isbn = JOptionPane.showInputDialog(this, "Enter ISBN to borrow:");
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        library.borrowBook(isbn, username);
        refreshBookTable();
    }

    private void returnBook() {
        String isbn = JOptionPane.showInputDialog(this, "Enter ISBN to return:");
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        library.returnBook(isbn, username);
        refreshBookTable();
    }

    private void generateReports() {
        library.generateReportsGUI();
    }
}
