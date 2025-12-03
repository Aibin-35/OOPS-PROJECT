package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.AuthController;
import model.User;

public class LoginView extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;

    public LoginView() {
        setTitle("IMS Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 243, 250));

        JLabel lblTitle = new JLabel("Inventory Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBounds(50, 20, 300, 30);
        add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(60, 80, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(160, 80, 170, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(60, 120, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 120, 170, 25);
       
        txtPassword.addActionListener(e -> btnLogin.doClick());
        add(txtPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setBounds(60, 160, 100, 25);
        add(lblRole);

        
        cmbRole = new JComboBox<>(new String[]{"Admin", "Employee"});
        cmbRole.setBounds(160, 160, 170, 25);
        add(cmbRole);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(140, 210, 120, 35);
        btnLogin.setBackground(new Color(102, 178, 255));
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(this);
        add(btnLogin);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != btnLogin) return;

        String uname = txtUsername.getText().trim();
        String pass  = new String(txtPassword.getPassword()).trim();
        String roleDisplay = (String) cmbRole.getSelectedItem();
        String roleDB = roleDisplay.equalsIgnoreCase("Admin") ? "ADMIN" : "EMPLOYEE"; // map to DB

        if (uname.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

       
        User user = AuthController.login(uname, pass, roleDB);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
            txtPassword.setText("");
            txtPassword.requestFocus();
            return;
        }

        
        if (!roleDB.equalsIgnoreCase(user.getRole())) {
            JOptionPane.showMessageDialog(this, "Role mismatch! You are not a " + roleDisplay);
            return;
        }

        dispose();
        if ("ADMIN".equals(user.getRole())) {
            new AdminView(user).setVisible(true);
        } else {
            new EmployeeView(user).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
