
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import controller.CategoryController;
import controller.ProductController;
import model.Category;
import model.Product;
import model.User;

public class AdminView extends JFrame implements ActionListener {

    private final User admin;

    // table
    private JTable tbl;
    private DefaultTableModel model;

    // form fields
    private JComboBox<String> cmbCategory;
    private JTextField txtName, txtQty, txtCost, txtExpiry;

    // buttons
    private JButton btnAdd, btnEdit, btnDelete, btnStockIn, btnStockOut,
            btnRefresh, btnAddCat, btnDelCat, btnReportM, btnReportY, btnLogout, btnManageEmp;

    public AdminView(User admin) {
        this.admin = admin;

        setTitle("Admin · IMS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 740);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        //  Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Welcome, " + admin.getFullname() + " (Admin)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        JLabel dateLabel = new JLabel("Date: " + LocalDate.now());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(dateLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Qty", "UnitCost", "Expiry", "StockStatus", "ExpiryStatus"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                return switch (col) {
                    case 0, 3 -> Integer.class;
                    case 4 -> Double.class;
                    default -> String.class;
                };
            }
        };

        tbl = new JTable(model);
        tbl.setRowHeight(26);
        tbl.setAutoCreateRowSorter(true); // enable sorting by header click
        tbl.setFillsViewportHeight(true);

        
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(220, 223, 228));
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // StockStatus / ExpiryStatus
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // reset
                if (!isSelected) {
                    comp.setForeground(Color.BLACK);
                    comp.setBackground(Color.WHITE);
                }

                String col = table.getColumnName(column);
                String cell = value == null ? "" : String.valueOf(value);

                if (!isSelected) {
                    if ("StockStatus".equals(col)) {
                        switch (cell.toUpperCase()) {
                            case "OUT" -> comp.setBackground(new Color(255, 119, 119));     // red
                            case "LOW" -> comp.setBackground(new Color(255, 214, 102));     // orange
                            case "MEDIUM" -> comp.setBackground(new Color(255, 245, 157));  // yellow
                            case "HIGH" -> comp.setBackground(new Color(163, 230, 163));    // green
                        }
                    }
                    if ("ExpiryStatus".equals(col)) {
                        switch (cell.toUpperCase()) {
                            case "EXPIRED" -> {
                                comp.setBackground(new Color(255, 77, 77));
                                comp.setForeground(Color.WHITE);
                            }
                            case "SOON" -> comp.setBackground(new Color(255, 214, 102));
                            case "OK" -> comp.setBackground(new Color(163, 230, 163));
                        }
                    }
                }
                return comp;
            }
        });

        JScrollPane sp = new JScrollPane(tbl);

        // fill form when row selected
        tbl.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tbl.getSelectedRow() >= 0) {
                int vr = tbl.getSelectedRow();
                int mr = tbl.convertRowIndexToModel(vr);
                txtName.setText(String.valueOf(model.getValueAt(mr, 1)));
                cmbCategory.setSelectedItem(String.valueOf(model.getValueAt(mr, 2)));
                txtQty.setText(String.valueOf(model.getValueAt(mr, 3)));
                txtCost.setText(String.valueOf(model.getValueAt(mr, 4)));
                txtExpiry.setText(String.valueOf(model.getValueAt(mr, 5)));
            }
        });

        //Product Details
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(0, 10, 0, 0));

      
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Product Details"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        txtName = new JTextField();
        cmbCategory = new JComboBox<>();
        txtQty = new JTextField();
        txtCost = new JTextField();
        txtExpiry = new JTextField();

        int row = 0;
        addFormRow(form, gc, row++, "Name", txtName);
        addFormRow(form, gc, row++, "Category", cmbCategory);
        addFormRow(form, gc, row++, "Quantity", txtQty);
        addFormRow(form, gc, row++, "Unit Cost", txtCost);
        addFormRow(form, gc, row++, "Expiry (YYYY-MM-DD)", txtExpiry);

        
        JPanel actions = new JPanel(new GridLayout(0, 2, 12, 12));
        actions.setBorder(BorderFactory.createTitledBorder("Actions"));

        btnAdd       = makeBtn("Add Product",      new Color(37, 99, 235),  Color.WHITE);
        btnEdit      = makeBtn("Edit Product",      new Color(37, 99, 235), Color.WHITE);
        btnDelete    = makeBtn("Delete Product",    new Color(37, 99, 235),  Color.WHITE);
        btnStockIn   = makeBtn("Stock IN",          new Color(37, 99, 235), Color.WHITE);
        btnStockOut  = makeBtn("Stock OUT",         new Color(37, 99, 235), Color.WHITE);
        btnRefresh   = makeBtn("Refresh",           new Color(37, 99, 235), Color.WHITE);
        btnAddCat    = makeBtn("Add Category",      new Color(37, 99, 235), Color.WHITE);
        btnDelCat    = makeBtn("Delete Category",   new Color(37, 99, 235),  Color.WHITE);
        btnReportM   = makeBtn("Monthly Report",    new Color(37, 99, 235),  Color.WHITE);
        btnReportY   = makeBtn("Yearly Report",     new Color(37, 99, 235),  Color.WHITE);
        btnManageEmp = makeBtn("Manage Employees",  new Color(37, 99, 235), Color.WHITE);
        btnLogout    = makeBtn("Logout",            new Color(220, 38, 38),  Color.WHITE); // RED

        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete, btnStockIn, btnStockOut,
                btnRefresh, btnAddCat, btnDelCat, btnReportM, btnReportY, btnManageEmp, btnLogout}) {
            b.addActionListener(this);
            actions.add(b);
        }

        right.add(form);
        right.add(Box.createVerticalStrut(10));
        right.add(actions);

        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, right);
        split.setResizeWeight(0.63);
        add(split, BorderLayout.CENTER);

        // data
        loadCategories();
        refreshProducts();
    }

    //helpers 

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(160, 40));
        return b;
    }

    private void addFormRow(JPanel form, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1; gc.anchor = GridBagConstraints.CENTER;
        form.add(field, gc);
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        List<Category> cats = CategoryController.getAll();
        for (Category c : cats) cmbCategory.addItem(c.getName());
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
    }

    // refresh  
    private void refreshProducts() {
        try {
            int deleted = ProductController.deleteExpired();
            var expSoon = ProductController.findExpiringSoon(7);
            var zero    = ProductController.findZeroStock();
            var low     = ProductController.findLowStock(5);

            model.setRowCount(0);
            for (Product p : ProductController.getAllWithStatus()) {
                model.addRow(new Object[]{
                        p.getId(), p.getName(), p.getCategoryName(),
                        p.getQuantity(), p.getUnitCost(),
                        (p.getExpiryDate() == null ? "" : p.getExpiryDate().toString()),
                        p.getStockStatus(), p.getExpiryStatus()
                });
            }

            StringBuilder alert = new StringBuilder();
            if (deleted > 0) alert.append("Deleted expired items: ").append(deleted).append("\n");
            if (!expSoon.isEmpty()) alert.append("Expiring within 7 days: ").append(String.join(", ", expSoon)).append("\n");
            if (!zero.isEmpty())    alert.append("Out of stock: ").append(String.join(", ", zero)).append("\n");
            if (!low.isEmpty())     alert.append("Low stock (<5): ").append(String.join(", ", low)).append("\n");
            if (alert.length() > 0)
                JOptionPane.showMessageDialog(this, alert.toString(), "Alerts", JOptionPane.WARNING_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Refresh failed: " + ex.getMessage());
        }
    }

    private Product fromForm(int id) {
        String name = txtName.getText().trim();
        String catName = (String) cmbCategory.getSelectedItem();
        int catId = CategoryController.getAll().stream()
                .filter(c -> c.getName().equals(catName))
                .findFirst().map(Category::getId).orElse(0);
        int qty = Integer.parseInt(blankZero(txtQty.getText()));
        double cost = Double.parseDouble(blankZero(txtCost.getText()));
        LocalDate exp = txtExpiry.getText().trim().isEmpty() ? null : LocalDate.parse(txtExpiry.getText().trim());
        return new Product(id, name, catId, catName, qty, cost, exp, admin.getId());
    }

    private String blankZero(String s) { return (s == null || s.trim().isEmpty()) ? "0" : s.trim(); }

    private void clearForm() { txtName.setText(""); txtQty.setText(""); txtCost.setText(""); txtExpiry.setText(""); }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }

    private boolean confirm(String s) {
        return JOptionPane.showConfirmDialog(this, s, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // actions 

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        try {
            if (s == btnAdd) {
                Product p = fromForm(0);
                ProductController.add(p);
                refreshProducts();
                clearForm();

            } else if (s == btnEdit) {
                int vr = tbl.getSelectedRow(); if (vr < 0) { msg("Select a row"); return; }
                int mr = tbl.convertRowIndexToModel(vr);
                int id = (int) model.getValueAt(mr, 0);
                Product p = fromForm(id);
                ProductController.update(p);
                refreshProducts();
                clearForm();

            } else if (s == btnDelete) {
                int vr = tbl.getSelectedRow(); if (vr < 0) { msg("Select a row"); return; }
                int mr = tbl.convertRowIndexToModel(vr);
                int id = (int) model.getValueAt(mr, 0);
                if (confirm("Delete product ID " + id + "?")) {
                    ProductController.delete(id);
                    refreshProducts();
                    clearForm();
                }

            } else if (s == btnStockIn) {
                int vr = tbl.getSelectedRow(); if (vr < 0) { msg("Select a row"); return; }
                int mr = tbl.convertRowIndexToModel(vr);
                int id = (int) model.getValueAt(mr, 0);

                new StockDialog(this, "Stock IN", (qty, price) -> {
                    try {
                        ProductController.stockIn(id, qty, price, admin.getId());
                        msg("Stock IN recorded.");
                        refreshProducts();
                    } catch (Exception ex) {
                        msg("Stock IN failed: " + ex.getMessage());
                    }
                }).setVisible(true);

            } else if (s == btnStockOut) {
                int vr = tbl.getSelectedRow(); if (vr < 0) { msg("Select a row"); return; }
                int mr = tbl.convertRowIndexToModel(vr);
                int id = (int) model.getValueAt(mr, 0);

                new StockDialog(this, "Stock OUT", (qty, price) -> {
                    try {
                        
                        ProductController.stockOut(id, qty, admin.getId(), "usage/sale", price);
                        msg("Stock OUT recorded.");
                        refreshProducts();
                    } catch (Exception ex) {
                        msg("Stock OUT failed: " + ex.getMessage());
                    }
                }, true).setVisible(true);

            } else if (s == btnRefresh) {
                loadCategories();
                refreshProducts();
                clearForm();

            } else if (s == btnAddCat) {
                String name = JOptionPane.showInputDialog(this, "New category:");
                if (name != null && !name.trim().isEmpty()) {
                    CategoryController.add(name.trim());
                    loadCategories();
                }

            } else if (s == btnDelCat) {
                String name = (String) JOptionPane.showInputDialog(this, "Delete which category?",
                        "Delete Category", JOptionPane.QUESTION_MESSAGE, null,
                        CategoryController.getAll().stream().map(Category::getName).toArray(), null);
                if (name != null && confirm("Delete category '" + name + "'?")) {
                    CategoryController.deleteByName(name);
                    loadCategories();
                }

            } else if (s == btnReportM) {
                new ReportDialog(this, ReportDialog.Mode.MONTHLY).setVisible(true);

            } else if (s == btnReportY) {
                new ReportDialog(this, ReportDialog.Mode.YEARLY).setVisible(true);

            } else if (s == btnManageEmp) {
               
                new EmployeeManagerDialog(this).setVisible(true);

            } else if (s == btnLogout) {
                dispose();
                new LoginView().setVisible(true);
            }

        } catch (Exception ex) {
            msg("Error: " + ex.getMessage());
        }
    }
}
