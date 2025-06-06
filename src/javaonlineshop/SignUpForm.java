package javaonlineshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpForm extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JCheckBox isAdminCheck;
    private JPanel panel;

    public SignUpForm() {
        setTitle("📝 FunkyShop Sign-Up");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setBackground(new Color(255, 182, 193)); // Light Pink (Matches login)
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Create Your Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        titleLabel.setBounds(60, 20, 320, 30);
        panel.add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 80, 200, 25);
        panel.add(usernameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 120, 100, 25);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 120, 200, 25);
        panel.add(emailField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 160, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 160, 200, 25);
        panel.add(passwordField);

        isAdminCheck = new JCheckBox("Sign up as Admin");
        isAdminCheck.setBounds(150, 195, 150, 25);
        panel.add(isAdminCheck);

        JButton signupBtn = new JButton("✅ Register");
        signupBtn.setBounds(60, 230, 120, 40);
        signupBtn.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        signupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(signupBtn);

        JButton loginBtn = new JButton("🔐 Back to Login");
        loginBtn.setBounds(200, 230, 120, 40);
        loginBtn.setBackground(new Color(135, 206, 250)); // Light Blue
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(loginBtn);

        signupBtn.addActionListener(this::registerUser);
        loginBtn.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void registerUser(ActionEvent e) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = isAdminCheck.isSelected();

        String query = isAdmin ? "INSERT INTO admin (username, password) VALUES (?, ?)"
                               : "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            if (!isAdmin) {
                stmt.setString(3, email);
            }

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, isAdmin ? "🎉 Admin Registered! Welcome to the dashboard." : "🎉 User Registered!");
                dispose();
                new LoginForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "⚠️ Registration Failed! Username might be taken.");
        }
    }
}
