package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StockDialog extends JDialog {
    public interface Callback {
        void apply(int qty, Double price);
    }

    public StockDialog(JFrame parent, String title, Callback cb) {
        this(parent, title, cb, false);
    }

    public StockDialog(JFrame parent, String title, Callback cb, boolean outMode) {
        super(parent, title, true);
        setSize(360, 250);
        setLocationRelativeTo(parent);
        setLayout(null);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setBounds(40, 30, 120, 30);
        JTextField txtQty = new JTextField();
        txtQty.setBounds(160, 30, 120, 30);
        add(lblQty); add(txtQty);

        JLabel lblPrice = new JLabel(outMode ? "Selling Price per unit:" : "Unit Cost per unit:");
        lblPrice.setBounds(40, 80, 160, 30);
        JTextField txtPrice = new JTextField();
        txtPrice.setBounds(200, 80, 80, 30);
        add(lblPrice); add(txtPrice);

        JButton btnOk = new JButton("OK");
        btnOk.setBounds(70, 140, 80, 30);
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(180, 140, 100, 30);
        add(btnOk); add(btnCancel);

        btnOk.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(txtQty.getText().trim());
                Double price = txtPrice.getText().trim().isEmpty() ? 0.0 :
                               Double.parseDouble(txtPrice.getText().trim());
                cb.apply(qty, price);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }
}
