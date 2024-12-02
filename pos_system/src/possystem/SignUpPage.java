package possystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class SignUpPage {
    
    // Declare the components for the UI
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signUpButton;
    
    public SignUpPage() {
        // Set up the UI components
        frame = new JFrame("Sign Up");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 30, 100, 25);
        frame.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(130, 30, 120, 25);
        frame.add(usernameField);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 70, 100, 25);
        frame.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(130, 70, 120, 25);
        frame.add(passwordField);
        
        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(90, 120, 100, 30);
        frame.add(signUpButton);
        
        // Add action listener for the SignUp button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUp();
            }
        });

        frame.setVisible(true);
    }
    
    private void signUp() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields.");
            return;
        }

        Connection connection = DbConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Sign up successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Sign up failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error.");
            }
        }
    }

    public static void main(String[] args) {
        new SignUpPage();
    }
}
