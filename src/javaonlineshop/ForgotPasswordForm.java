package javaonlineshop;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class ForgotPasswordForm {

    JFrame frame;
    JTextField emailField;

    public ForgotPasswordForm() {
        frame = new JFrame("🔐 Forgot Password");
        frame.setSize(400, 250);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(255, 248, 220)); // Light golden

        JLabel titleLabel = new JLabel("🔑 Reset Your Password", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        titleLabel.setBounds(40, 20, 320, 30);
        frame.add(titleLabel);

        JLabel emailLabel = new JLabel("Enter your email:");
        emailLabel.setBounds(50, 70, 300, 25);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        frame.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(50, 100, 280, 30);
        frame.add(emailField);

        JButton resetBtn = new JButton("🔁 Reset Password");
        resetBtn.setBounds(100, 150, 200, 40);
        resetBtn.setFont(new Font("Arial", Font.BOLD, 14));
        resetBtn.setBackground(new Color(60, 179, 113)); // Sea Green
        resetBtn.setForeground(Color.WHITE);
        frame.add(resetBtn);

        resetBtn.addActionListener(e -> resetPassword());

        frame.setVisible(true);
    }

    private void resetPassword() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "❗ Email is required.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?")) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String newPassword = JOptionPane.showInputDialog(frame, "Enter New Password:");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    PreparedStatement update = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
                    update.setString(1, newPassword);
                    update.setString(2, email);
                    update.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "✅ Password updated successfully!");
                    frame.dispose();
                    new LoginForm();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "❌ No user found with this email!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
