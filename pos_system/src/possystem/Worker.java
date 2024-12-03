
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */package possystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author PCS
 */
public class Worker extends JFrame {
    private DefaultListModel<String> productListModel;
    private ArrayList<Product> currentBill;
    private double totalBillAmount = 0.0;

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
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT price, quantity FROM products WHERE name = ?")) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("quantity");

                    if (quantity > stock) {
                        JOptionPane.showMessageDialog(this, "Insufficient stock for " + productName, "Stock Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE products SET quantity = quantity - ? WHERE name = ?")) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setString(2, productName);
                        updateStmt.executeUpdate();
                    }
                    currentBill.add(new Product(productName, price, quantity));
                    totalBillAmount += price * quantity;

                    JOptionPane.showMessageDialog(this, "Added to bill: " + productName + " - " + quantity);
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void viewBill() {
        if (currentBill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items in the bill!", "Bill Details", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder billDetails = new StringBuilder();
        billDetails.append("Current Bill:\n");
        billDetails.append("--------------------------------------------------\n");
        billDetails.append(String.format("%-20s %-10s %-10s\n", "Product", "Quantity", "Price"));
        billDetails.append("--------------------------------------------------\n");

        for (Product product : currentBill) {
            billDetails.append(String.format("%-20s %-10d %-10.2f\n", product.getName(), product.getQuantity(), product.getPrice() * product.getQuantity()));
        }
        billDetails.append("--------------------------------------------------\n");
        billDetails.append(String.format("Total Amount: Rs %.2f\n", totalBillAmount));
        JOptionPane.showMessageDialog(this, billDetails.toString(), "Bill Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
