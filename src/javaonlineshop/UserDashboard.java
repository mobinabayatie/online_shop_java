package javaonlineshop;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JTable productTable, cartTable, orderTable;
    private DefaultTableModel productModel, cartModel, orderModel;
    private JLabel totalPriceLabel;
    private double totalPrice = 0.0;
    private int userId;
    private String username;

    public UserDashboard(int userId, String username) {
        this.userId = userId;
        this.username = username;
        setTitle("🛍 Welcome, " + username);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Navigation Panel
        JPanel sidePanel = new JPanel(new GridLayout(6, 1));
        sidePanel.setBackground(new Color(25, 25, 112));

        JButton shopBtn = createStyledButton("🛒 Shop Products", new Color(50, 205, 50));
        JButton cartBtn = createStyledButton("🛍 View Cart", new Color(255, 165, 0));
        JButton orderHistoryBtn = createStyledButton("📜 Order History", new Color(70, 130, 180));
        JButton logoutBtn = createStyledButton("🚪 Logout", new Color(255, 69, 0));
        JButton backBtn = createStyledButton("🔙 Back", new Color(70, 130, 180));

        sidePanel.add(shopBtn);
        sidePanel.add(cartBtn);
        sidePanel.add(orderHistoryBtn);
        sidePanel.add(logoutBtn);
        sidePanel.add(backBtn);

        // Main Content Panel
        mainPanel = new JPanel(new CardLayout());

        JPanel shopPanel = createShopPanel();
        JPanel cartPanel = createCartPanel();
        JPanel orderHistoryPanel = createOrderHistoryPanel();

        mainPanel.add(shopPanel, "Shop");
        mainPanel.add(cartPanel, "Cart");
        mainPanel.add(orderHistoryPanel, "OrderHistory");

        shopBtn.addActionListener(e -> switchPanel("Shop"));
        cartBtn.addActionListener(e -> switchPanel("Cart"));
        orderHistoryBtn.addActionListener(e -> switchPanel("OrderHistory"));
        logoutBtn.addActionListener(e -> logout());
        backBtn.addActionListener(e -> goBack());

        add(sidePanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }

    private JPanel createShopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 224));
        productModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0);
        productTable = new JTable(productModel);
        loadProducts();

        JButton addToCartBtn = createStyledButton("🛒 Add to Cart", new Color(50, 205, 50));
        addToCartBtn.addActionListener(e -> addToCart());

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(addToCartBtn, BorderLayout.SOUTH);
        return panel;
    }
    private void loadProducts() {
        productModel.setRowCount(0); // Clear previous data
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM products");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"),
                    rs.getDouble("price"), rs.getInt("quantity")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error loading products!");
        }
    }


    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 228, 225));
        cartModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0);
        cartTable = new JTable(cartModel);

        JButton removeFromCartBtn = createStyledButton("❌ Remove from Cart", new Color(255, 69, 0));
        JButton checkoutBtn = createStyledButton("💳 Checkout", new Color(255, 215, 0));

        removeFromCartBtn.addActionListener(e -> removeFromCart());
        checkoutBtn.addActionListener(e -> checkout());

        totalPriceLabel = new JLabel("Total Price: $0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalPriceLabel.setForeground(new Color(0, 100, 0));

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        panel.add(totalPriceLabel, BorderLayout.NORTH);
        panel.add(removeFromCartBtn, BorderLayout.WEST);
        panel.add(checkoutBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 235, 205));

        orderModel = new DefaultTableModel(new String[]{"Order ID", "Product", "Price", "Quantity", "Date"}, 0);
        orderTable = new JTable(orderModel);
        loadUserOrders();

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        return panel;
    }

    private void checkout() {
        if (!userExists(userId)) {
            JOptionPane.showMessageDialog(this, "❌ Error: User ID not found!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO orders (user_id, product_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)")) {
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                ps.setInt(1, userId);
                ps.setInt(2, (int) cartModel.getValueAt(i, 0));
                ps.setString(3, (String) cartModel.getValueAt(i, 1));
                ps.setDouble(4, (double) cartModel.getValueAt(i, 2));
                ps.setInt(5, (int) cartModel.getValueAt(i, 3));
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "✅ Purchase Completed!");
            cartModel.setRowCount(0);
            totalPrice = 0.0;
            totalPriceLabel.setText("Total Price: $0.00");
            loadUserOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean userExists(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();  // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void loadUserOrders() {
        orderModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orderModel.addRow(new Object[]{rs.getInt("order_id"), rs.getString("product_name"), rs.getDouble("price"), rs.getInt("quantity"), rs.getTimestamp("order_date")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // Method to switch between panels
    private void switchPanel(String panelName) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, panelName);
    }

    // Method to add products to the cart
    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a product to add to cart!");
            return;
        }

        Object[] rowData = new Object[4];
        for (int i = 0; i < 4; i++) {
            rowData[i] = productModel.getValueAt(selectedRow, i);
        }
        cartModel.addRow(rowData);
        totalPrice += (double) rowData[2];  // Add price to total
        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
        JOptionPane.showMessageDialog(this, "✅ Product added to cart!");
    }

    // Method to remove products from the cart
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Select a product to remove from cart!");
            return;
        }

        totalPrice -= (double) cartModel.getValueAt(selectedRow, 2); // Deduct price from total
        cartModel.removeRow(selectedRow);
        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
        JOptionPane.showMessageDialog(this, "❌ Product removed from cart!");
    }

    // Method to log out and return to the login screen
    private void logout() {
        JOptionPane.showMessageDialog(this, "🚪 Logging out...");
        dispose();
        new LoginForm(); // Ensure LoginForm exists
    }

    // Method to go back to the login screen
    private void goBack() {
        JOptionPane.showMessageDialog(this, "🔙 Going back...");
        dispose();
        new LoginForm(); // Ensure LoginForm exists
    }

}
