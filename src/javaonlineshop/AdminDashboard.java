package javaonlineshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private JPanel mainPanel;
    private JTable productTable, userTable, adminTable;
    private DefaultTableModel productModel, userModel, adminModel;

    public AdminDashboard() {
        setTitle("🚀 Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Navigation Panel
        JPanel sidePanel = new JPanel(new GridLayout(5, 1));
        sidePanel.setBackground(new Color(30, 30, 30)); 

        JButton productBtn = new JButton("📦 Manage Products");
        JButton userBtn = new JButton("👥 Manage Users");
        JButton adminBtn = new JButton("🔑 Manage Admins");
        JButton orderBtn = new JButton("📊 View Orders");
        JButton discountBtn = new JButton("💰 Apply Discounts");
        JButton logoutBtn = new JButton("🚪 Logout");
        
        
        sidePanel.add(logoutBtn);
        sidePanel.add(productBtn);
        sidePanel.add(userBtn);
        sidePanel.add(adminBtn);
        sidePanel.add(orderBtn);
        sidePanel.add(discountBtn);

        mainPanel = new JPanel(new CardLayout());

        JPanel productPanel = createProductPanel();
        JPanel userPanel = createUserPanel();
        JPanel adminPanel = createAdminPanel();
        JPanel orderPanel = createOrderPanel();
        JPanel discountPanel = createDiscountPanel();

        mainPanel.add(productPanel, "Products");
        mainPanel.add(userPanel, "Users");
        mainPanel.add(adminPanel, "Admins");
        mainPanel.add(orderPanel, "Orders");
        mainPanel.add(discountPanel, "Discounts");

        productBtn.addActionListener(e -> switchPanel("Products"));
        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "🚪 Logging out...");
            dispose();
            new LoginForm();
        });
        userBtn.addActionListener(e -> switchPanel("Users"));
        adminBtn.addActionListener(e -> switchPanel("Admins"));
        orderBtn.addActionListener(e -> switchPanel("Orders"));
        discountBtn.addActionListener(e -> switchPanel("Discounts"));

        add(sidePanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    

    private void switchPanel(String panelName) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, panelName);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        productModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0);
        productTable = new JTable(productModel);
        loadProducts();

        JButton addProductBtn = new JButton("➕ Add Product");
        JButton deleteProductBtn = new JButton("❌ Delete Product");
        JButton restockBtn = new JButton("📦 Restock Product");
        restockBtn.addActionListener(e -> restockProduct());


        addProductBtn.addActionListener(e -> addProduct());
        deleteProductBtn.addActionListener(e -> deleteProduct());

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(addProductBtn, BorderLayout.NORTH);
        panel.add(deleteProductBtn, BorderLayout.SOUTH);
        panel.add(restockBtn, BorderLayout.EAST);
        return panel;
    }

    private void restockProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a product to restock!");
            return;
        }

        int productId = (int) productModel.getValueAt(selectedRow, 0);
        String input = JOptionPane.showInputDialog(this, "Enter quantity to add:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int addQuantity = Integer.parseInt(input);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE products SET quantity = quantity + ? WHERE id = ?")) {
                ps.setInt(1, addQuantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Stock updated!");
                productModel.setRowCount(0);
                loadProducts();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid number!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



	private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        userModel = new DefaultTableModel(new String[]{"ID", "Username", "Email"}, 0);
        userTable = new JTable(userModel);
        loadUsers();

        JButton deleteUserBtn = new JButton("❌ Delete User");
        deleteUserBtn.addActionListener(e -> deleteUser());

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(deleteUserBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        adminModel = new DefaultTableModel(new String[]{"ID", "Username"}, 0);
        adminTable = new JTable(adminModel);
        loadAdmins();

        JButton addAdminBtn = new JButton("➕ Add Admin");
        JButton deleteAdminBtn = new JButton("❌ Remove Admin");

        addAdminBtn.addActionListener(e -> addAdmin());
        deleteAdminBtn.addActionListener(e -> deleteAdmin());

        panel.add(new JScrollPane(adminTable), BorderLayout.CENTER);
        panel.add(addAdminBtn, BorderLayout.NORTH);
        panel.add(deleteAdminBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel orderModel = new DefaultTableModel(
            new String[]{"Order ID", "User ID", "Product", "Price", "Quantity", "Date"}, 0);
        JTable orderTable = new JTable(orderModel);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orderModel.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getString("product_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("order_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        return panel;
    }
    private JPanel createDiscountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton applyDiscountBtn = new JButton("💰 Apply Discount");

        applyDiscountBtn.addActionListener(e -> applyDiscount());

        panel.add(new JLabel("💰 Set product discounts here"), BorderLayout.NORTH);
        panel.add(applyDiscountBtn, BorderLayout.CENTER);
        return panel;
    }

    private void loadProducts() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM products");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("quantity")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("email")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAdmins() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM admin");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                adminModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String name = JOptionPane.showInputDialog("Enter Product Name:");
        String price = JOptionPane.showInputDialog("Enter Price:");
        String quantity = JOptionPane.showInputDialog("Enter Quantity:");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setDouble(2, Double.parseDouble(price));
            ps.setInt(3, Integer.parseInt(quantity));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Product Added!");
            productModel.setRowCount(0);
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyDiscount() {
        String discount = JOptionPane.showInputDialog("Enter Discount Percentage:");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE products SET price = price - (price * ? / 100)")) {
            ps.setDouble(1, Double.parseDouble(discount));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "💰 Discount Applied!");
            productModel.setRowCount(0);
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void addAdmin() {
        String username = JOptionPane.showInputDialog("Enter Admin Username:");
        String password = JOptionPane.showInputDialog("Enter Admin Password:");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO admin (username, password) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Admin Added!");
            adminModel.setRowCount(0); // Refresh admin table
            loadAdmins();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAdmin() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select an Admin to delete!");
            return;
        }

        int adminId = (int) adminModel.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM admin WHERE id = ?")) {
            ps.setInt(1, adminId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Admin Removed!");
            adminModel.removeRow(selectedRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a User to delete!");
            return;
        }

        int userId = (int) userModel.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ User Deleted!");
            userModel.removeRow(selectedRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a Product to delete!");
            return;
        }

        int productId = (int) productModel.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Product Removed!");
            productModel.removeRow(selectedRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    


}
