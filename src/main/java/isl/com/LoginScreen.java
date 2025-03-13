package isl.com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD_HASH = HashUtil.hashPassword("admin");

    public LoginScreen() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginFrame.add(userLabel);
        loginFrame.add(userField);
        loginFrame.add(passLabel);
        loginFrame.add(passField);
        loginFrame.add(new JLabel());
        loginFrame.add(loginButton);

        loginFrame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                if (username.equals(ADMIN_USERNAME) && HashUtil.hashPassword(password).equals(ADMIN_PASSWORD_HASH)) {
                    loginFrame.dispose();
                    new VendingMachineGUI();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials!", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
