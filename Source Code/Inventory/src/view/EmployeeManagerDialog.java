// src/view/EmployeeManagerDialog.java
package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeManagerDialog extends JDialog {
    private JTable tbl;
    private DefaultTableModel model;
    private JTextField txtName, txtUsername;
    private JPasswordField txtPassword;
    private JButton btnAdd, btnDelete, btnClose, btnRefresh;

    public EmployeeManagerDialog(Frame owner) {
        super(owner, "Manage Employees", true);
        setSize(700, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));

        // table
        model = new DefaultTableModel(new Object[]{"ID","Full Name","Username"}, 0){
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tbl = new JTable(model);
        tbl.setRowHeight(22);
        tbl.setAutoCreateRowSorter(true);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // form panel
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.setBorder(BorderFactory.createTitledBorder("Add New Employee"));
        txtName = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        form.add(new JLabel("Full Name:"));   form.add(txtName);
        form.add(new JLabel("Username:"));    form.add(txtUsername);
        form.add(new JLabel("Password:"));    form.add(txtPassword);

        btnAdd = makeBtn("Add Employee", new Color(37, 99, 235), Color.WHITE);
        btnAdd.addActionListener(e -> addEmployee());
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(btnAdd, BorderLayout.SOUTH);

        // right side buttons
        JPanel right = new JPanel(new GridLayout(0,1,6,6));
        right.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        btnDelete = makeBtn("Delete Selected", new Color(37, 99, 235), Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh = makeBtn("Refresh", new Color(37, 99, 235), Color.WHITE); 
        btnRefresh.addActionListener(e -> refresh());
        btnClose = makeBtn("Close", new Color(239, 68, 68), Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        right.add(btnDelete); right.add(btnRefresh); right.add(btnClose);

        add(formWrap, BorderLayout.NORTH);
        add(right, BorderLayout.EAST);

        refresh();
    }
    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(140, 35));
        return b;
    }

    private void refresh() {
        model.setRowCount(0);
        List<User> emps = UserController.listEmployees();
        for (User u : emps) {
            model.addRow(new Object[]{u.getId(), u.getFullname(), u.getUsername()});
        }
    }
    //add employee
    private void addEmployee() {
        String name = txtName.getText().trim();
        String uname = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (name.isEmpty() || uname.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required."); return;
        }
        if (UserController.usernameExists(uname)) {
            JOptionPane.showMessageDialog(this, "Username already exists."); return;
        }
        if (UserController.addEmployee(name, uname, pass)) {
            JOptionPane.showMessageDialog(this, "Employee added.");
            txtName.setText(""); txtUsername.setText(""); txtPassword.setText("");
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add employee.");
        }
    }
    //delete employee
    private void deleteSelected() {
        int viewRow = tbl.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Select a row."); return; }
        int row = tbl.convertRowIndexToModel(viewRow);
        int id = (int) model.getValueAt(row, 0);
        String name = String.valueOf(model.getValueAt(row, 1));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete employee: " + name + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (UserController.deleteEmployee(id)) {
            JOptionPane.showMessageDialog(this, "Employee deleted.");
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed.");
        }
    }
}
