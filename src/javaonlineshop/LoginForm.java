package javaonlineshop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javaonlineshop.RegisterForm;


public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox isAdminCheck;
    private JPanel panel;

    public LoginForm() {
        setTitle("🌟 FunkyShop Login 🌟");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setBackground(new Color(255, 215, 0)); // Bright Gold
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("FunkyShop Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setBounds(60, 20, 320, 30);
        panel.add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 80, 200, 25);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 120, 200, 25);
        panel.add(passwordField);

        isAdminCheck = new JCheckBox("Login as Admin");
        isAdminCheck.setBounds(150, 160, 150, 25);
        panel.add(isAdminCheck);

        JButton loginBtn = new JButton("🚀 Login");
        loginBtn.setBounds(50, 200, 120, 40);
        loginBtn.setBackground(new Color(135, 206, 250)); 
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(loginBtn);

        JButton signupBtn = new JButton("📝 Sign Up");
        signupBtn.setBounds(190, 200, 120, 40);
        signupBtn.setBackground(new Color(144, 238, 144)); 
        signupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(signupBtn);

        JButton forgotBtn = new JButton("🔑 Forgot Password");
        forgotBtn.setBounds(110, 260, 170, 35);
        forgotBtn.setBackground(Color.LIGHT_GRAY);
        panel.add(forgotBtn);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                boolean isAdmin = isAdminCheck.isSelected();

                if (DBConnection.validateUser(username, password, isAdmin)) {
                    dispose();
                    if (isAdmin) {
                        new AdminDashboard();
                    } else {
                        int userId = DBConnection.getUserIdByUsername(username);
                        new UserDashboard(userId, username);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signupBtn.addActionListener(e -> {
            dispose();
            new SignUpForm();
        });


        forgotBtn.addActionListener(e -> {
            dispose();
            new ForgotPasswordForm();
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}
