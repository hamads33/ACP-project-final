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
public class Admin extends JFrame {
    private DefaultListModel<String> productListModel;

    public Admin() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productListModel = new DefaultListModel<>();
        loadProducts();

        JList<String> productList = new JList<>(productListModel);
        JScrollPane scrollPane = new JScrollPane(productList);

        JButton addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(e -> addProduct());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addProductButton);
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

    private void addProduct() {
        String name = JOptionPane.showInputDialog(this, "Enter product name:");
        if (name == null || name.isEmpty()) return;

        double price = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter product price:"));
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter product quantity:"));

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
            loadProducts();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
