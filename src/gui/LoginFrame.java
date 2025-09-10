package src.gui;

import model.User;
import service.Library;
import service.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private UserManager userManager;
    private Library library;

    public LoginFrame(UserManager um, Library lib) {
        this.userManager = um;
        this.library = lib;
        setTitle("Library Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 50, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 50, 180, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 90, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 90, 180, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(50, 140, 120, 30);
        add(btnLogin);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(210, 140, 120, 30);
        add(btnRegister);

        btnLogin.addActionListener(e -> loginAction());
        btnRegister.addActionListener(e -> registerAction());

        setVisible(true);
    }

    private void loginAction() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());
        User user = userManager.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getUsername());
            dispose();
            if (user.getRole().equalsIgnoreCase("Admin"))
                new AdminDashboard(library, userManager, user);
            else
                new UserDashboard(library, userManager, user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerAction() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());
        if (userManager.userExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        } else {
            userManager.addUser(username, password, "User");
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
        }
    }
}
