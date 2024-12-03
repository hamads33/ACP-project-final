/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
/**
 *
 * @author PCS
 */
public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        // Frame setup
        setTitle("POS System - Login");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10)); // Adjusted layout for an extra button

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        // Buttons
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login()); // Login action

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(e -> openSignUpPage()); // Sign Up action

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0)); // Exit action

        // Add components to the frame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);
        add(exitButton);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Database query for login
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {

            // Set parameters
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Execute query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if (role.equalsIgnoreCase("Admin")) {
                        JOptionPane.showMessageDialog(this, "Welcome Admin!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                        new Admin().setVisible(true); // Open Admin Dashboard
                    } else if (role.equalsIgnoreCase("Worker")) {
                        JOptionPane.showMessageDialog(this, "Welcome Worker!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                        new Worker().setVisible(true); // Open Worker Dashboard
                    } else {
                        JOptionPane.showMessageDialog(this, "Unknown role. Contact Admin.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    this.dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSignUpPage() {
        // Close the current login page and open the sign-up page
        this.dispose();
        SwingUtilities.invokeLater(SignUpPage::new);
    }

    public static void main(String[] args) {
        // Run LoginPage on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
