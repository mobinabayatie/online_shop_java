package javaonlineshop;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

class RegisterForm {
    JFrame frame;
    JTextField usernameField, emailField;
    JPasswordField passwordField;

    public RegisterForm() {
        frame = new JFrame("Register Page");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 1));

        usernameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();

        JButton registerBtn = new JButton("Register");
        frame.add(new JLabel("Username:"));
        frame.add(usernameField);
        frame.add(new JLabel("Email:"));
        frame.add(emailField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(registerBtn);

        registerBtn.addActionListener(e -> register());

        frame.setVisible(true);
    }

    private void register() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");) {

            ps.setString(1, usernameField.getText());
            ps.setString(2, new String(passwordField.getPassword()));
            ps.setString(3, emailField.getText());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Registered successfully!");
            frame.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
