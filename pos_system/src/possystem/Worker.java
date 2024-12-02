package possystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class Worker extends JFrame {
    private DefaultListModel<String> productListModel;
    private ArrayList<Product> currentBill;

    public Worker() {
        setTitle("Worker Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentBill = new ArrayList<>();
        productListModel = new DefaultListModel<>();
        loadProducts();

        JList<String> productList = new JList<>(productListModel);
        JScrollPane scrollPane = new JScrollPane(productList);

        JButton createBillButton = new JButton("Create Bill");
        createBillButton.addActionListener(e -> createBill());

        JButton viewBillButton = new JButton("View Bill");
        viewBillButton.addActionListener(e -> viewBill());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createBillButton);
        buttonPanel.add(viewBillButton);
        buttonPanel.add(logoutButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        productListModel.clear();
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            while (rs.next()) {
                String product = rs.getString("name") + " - Rs" + rs.getDouble("price") + " - Qty: " + rs.getInt("quantity");
                productListModel.addElement(product);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createBill() {
        String input = JOptionPane.showInputDialog(this, "Enter product number to purchase:");
        if (input == null || input.isEmpty()) return;

        try {
            int productIndex = Integer.parseInt(input) - 1;
            if (productIndex < 0 || productIndex >= productListModel.size()) {
                JOptionPane.showMessageDialog(this, "Invalid product number.");
                return;
            }

            String selectedProduct = productListModel.getElementAt(productIndex);
            String productName = selectedProduct.split(" - ")[0];

            String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity to purchase:");
            if (quantityStr == null || quantityStr.isEmpty()) return;
            int quantity = Integer.parseInt(quantityStr);

            addToBill(productName, quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input!");
        }
    }

    private void addToBill(String productName, int quantity) {
        // Add logic to reduce product stock and calculate bill amount
        JOptionPane.showMessageDialog(this, "Added to bill: " + productName + " - " + quantity);
    }

    private void viewBill() {
        // Display current bill
        JOptionPane.showMessageDialog(this, "View current bill here!");
    }
}
