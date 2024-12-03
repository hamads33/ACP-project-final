/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
/**
 *
 * @author PCS
 */
public class SignUpPage {

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox; // Role dropdown
    private JButton signUpButton;

    public SignUpPage() {
        // Use SwingUtilities to ensure UI runs on EDT
        SwingUtilities.invokeLater(this::createUI);
    }

    private void createUI() {
        // Set up the frame
        frame = new JFrame("Sign Up");
        frame.setSize(350, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 30, 100, 25);
        frame.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(140, 30, 150, 25);
        frame.add(usernameField);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 70, 100, 25);
        frame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 70, 150, 25);
        frame.add(passwordField);

        // Role Label and Dropdown
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 110, 100, 25);
        frame.add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"Admin", "Worker"});
        roleComboBox.setBounds(140, 110, 150, 25);
        frame.add(roleComboBox);

        // Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(100, 160, 120, 30);
        frame.add(signUpButton);

        // Add ActionListener for the Sign Up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUp();
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }

    private void signUp() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleComboBox.getSelectedItem().toString(); // Get selected role

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Attempt to connect to the database
        Connection connection = DbConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role); // Save the selected role

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Close Sign Up page and open Login page
                    frame.dispose();
                    new LoginPage().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "Sign up failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Launch SignUpPage on the Event Dispatch Thread
        SwingUtilities.invokeLater(SignUpPage::new);
    }
}
