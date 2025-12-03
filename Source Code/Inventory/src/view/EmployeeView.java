package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

import controller.CategoryController;
import controller.ProductController;
import model.Category;
import model.Product;
import model.User;

public class EmployeeView extends JFrame implements ActionListener {
    private final User emp;
    private JTable tbl;
    private DefaultTableModel model;
    private JButton btnIn, btnOut, btnRefresh, btnLogout;
    private JButton btnAddProd, btnEditProd, btnDelProd, btnAddCat, btnDelCat;
    public EmployeeView(User emp) {
        this.emp = emp;
        // Frame 
        setTitle("Employee · IMS");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBounds(0, 0, getWidth(), 44);
        header.setBackground(new Color(230, 255, 240));
        header.setBorder(new EmptyBorder(10, 16, 10, 16));
        add(header);

        JLabel head = new JLabel("Welcome, " + emp.getFullname() + " (Employee)");
        head.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(head, BorderLayout.WEST);

        // Date 
        JLabel dateLabel = new JLabel("Date: " + LocalDate.now());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.add(dateLabel, BorderLayout.CENTER);

        btnLogout = makeBtn("Logout", new Color(220, 38, 38), Color.WHITE); // RED
        btnLogout.setBounds(880, 8, 100, 28);
        btnLogout.addActionListener(this);
        add(btnLogout);

        //Table
        model = new DefaultTableModel(
                new Object[]{"ID","Name","Category","Qty","UnitCost","Expiry","StockStatus","ExpiryStatus"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0,3 -> Integer.class;
                    case 4 -> Double.class;
                    default -> String.class;
                };
            }
        };

        tbl = new JTable(model);
        tbl.setAutoCreateRowSorter(true);
        tbl.setRowHeight(24);
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(220, 223, 228));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // StockStatus / ExpiryStatus
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);

                    String col = table.getColumnName(column);
                    if ("StockStatus".equals(col)) {
                        String v = String.valueOf(value);
                        if ("OUT".equalsIgnoreCase(v))        c.setBackground(new Color(255,102,102)); // red
                        else if ("LOW".equalsIgnoreCase(v))    c.setBackground(new Color(255,204,102)); // orange
                        else if ("MEDIUM".equalsIgnoreCase(v)) c.setBackground(new Color(255,255,153)); // yellow
                        else if ("HIGH".equalsIgnoreCase(v))   c.setBackground(new Color(153,255,153)); // green
                    } else if ("ExpiryStatus".equals(col)) {
                        String v = String.valueOf(value);
                        if ("EXPIRED".equalsIgnoreCase(v)) {    // red + white text
                            c.setBackground(new Color(255,77,77));
                            c.setForeground(Color.WHITE);
                        } else if ("SOON".equalsIgnoreCase(v))  c.setBackground(new Color(255,204,102)); // orange
                        else if ("OK".equalsIgnoreCase(v))      c.setBackground(new Color(153,255,153)); // green
                    }
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBounds(20, 60, 760, 500);
        add(sp);

        // Actions panel
        JPanel actions = new JPanel(new GridLayout(0,1,12,12));
        actions.setBorder(BorderFactory.createTitledBorder("Actions"));
        actions.setBounds(800, 60, 170, 500);
        add(actions);

        // stock buttons 
        btnIn = makeBtn("Stock IN", new Color(16,185,129), Color.WHITE);
        btnOut = makeBtn("Stock OUT", new Color(16,185,129), Color.BLACK);
        btnRefresh = makeBtn("Refresh", new Color(16,185,129), Color.WHITE);

        // NEW manage buttons
        Color blue = new Color(37,99,235);
        btnAddProd = makeBtn("Add Product", blue, Color.WHITE);
        btnEditProd = makeBtn("Edit Selected", blue, Color.WHITE);
        btnDelProd = makeBtn("Delete Selected", blue, Color.WHITE);
        btnAddCat = makeBtn("Add Category", blue, Color.WHITE);
        btnDelCat = makeBtn("Delete Category", blue, Color.WHITE);

        //listeners
        for (JButton b : new JButton[]{btnIn, btnOut, btnRefresh, btnAddProd, btnEditProd, btnDelProd, btnAddCat, btnDelCat}) {
            b.addActionListener(this);
            actions.add(b);
        }

        refreshProducts();
    }

    /** Coloured buttons */
    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(150, 36));
        return b;
    }

    private void refreshProducts() {
        model.setRowCount(0);
        for (Product p : ProductController.getAllWithStatus()) {
            model.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategoryName(),
                    p.getQuantity(), p.getUnitCost(),
                    (p.getExpiryDate()==null? "" : p.getExpiryDate().toString()),
                    p.getStockStatus(), p.getExpiryStatus()
            });
        }
    }

    // ===== selection of products
    private int selectedProductIdOrWarn() {
        int vr = tbl.getSelectedRow();
        if (vr < 0) { msg("Select a product row first."); return -1; }
        int mr = tbl.convertRowIndexToModel(vr);
        return (int) model.getValueAt(mr, 0);
    }

    private void msg(String s){ JOptionPane.showMessageDialog(this, s); }

    private static String z(String s){ return (s==null || s.trim().isEmpty()) ? "0" : s.trim(); }

    // Action handling
    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        try {
            // stock buttons
            if (s == btnRefresh) {
                refreshProducts();
                //Stock IN
            } else if (s == btnIn) {
                int id = selectedProductIdOrWarn(); if (id < 0) return;
                new StockDialog(this, "Stock IN", (qty, price) -> {
                    try {
                        ProductController.stockIn(id, qty, price, emp.getId());
                        msg("Stock IN recorded.");
                        refreshProducts();
                    } catch (Exception ex) { msg("Stock IN failed: " + ex.getMessage()); }
                }).setVisible(true);
                
                //Stock OUT
            } else if (s == btnOut) {
                int id = selectedProductIdOrWarn(); if (id < 0) return;
                new StockDialog(this, "Stock OUT", (qty, price) -> {
                    try {
                        ProductController.stockOut(id, qty, emp.getId(), "sale/usage", price);
                        msg("Stock OUT recorded.");
                        refreshProducts();
                    } catch (Exception ex) { msg("Stock OUT failed: " + ex.getMessage()); }
                }, /*outMode=*/true).setVisible(true);

                //LOGOUT
            } else if (s == btnLogout) {
                dispose();
                new LoginView().setVisible(true);

            // product management
            } else if (s == btnAddProd) {
                openProductDialog(false, 0);

            } else if (s == btnEditProd) {
                int id = selectedProductIdOrWarn(); if (id < 0) return;
                openProductDialog(true, id);

            } else if (s == btnDelProd) {
                int id = selectedProductIdOrWarn(); if (id < 0) return;
                if (JOptionPane.showConfirmDialog(this, "Delete product ID " + id + "?",
                        "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    ProductController.delete(id);
                    refreshProducts();
                }

            // category management
            } else if (s == btnAddCat) {
                String name = JOptionPane.showInputDialog(this, "New category name:");
                if (name != null && !name.trim().isEmpty()) {
                    CategoryController.add(name.trim());
                    msg("Category added.");
                }

            } else if (s == btnDelCat) {
                List<Category> cats = CategoryController.getAll();
                if (cats.isEmpty()) { msg("No categories to delete."); return; }
                String[] options = cats.stream().map(Category::getName).toArray(String[]::new);
                String pick = (String) JOptionPane.showInputDialog(this, "Select category to delete:",
                        "Delete Category", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (pick != null) {
                    if (JOptionPane.showConfirmDialog(this, "Delete category '" + pick + "'?",
                            "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        CategoryController.deleteByName(pick);
                        msg("Category deleted.");
                    }
                }
            }

        } catch (Exception ex) {
            msg("Error: " + ex.getMessage());
        }
    }

    // Product Add/Edit dialog
    private void openProductDialog(boolean edit, int productId) {
        JDialog d = new JDialog(this, edit ? "Edit Product" : "Add Product", true);
        d.setSize(420, 360);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JTextField txtName = new JTextField();
        JComboBox<String> cmbCat = new JComboBox<>();
        JTextField txtQty = new JTextField();
        JTextField txtCost = new JTextField();
        JTextField txtExp = new JTextField();

        // categories
        List<Category> cats = CategoryController.getAll();
        for (Category c : cats) cmbCat.addItem(c.getName());

        int row = 0;
        addRow(d, gc, row++, "Name", txtName);
        addRow(d, gc, row++, "Category", cmbCat);
        addRow(d, gc, row++, "Quantity", txtQty);
        addRow(d, gc, row++, "Unit Cost", txtCost);
        addRow(d, gc, row++, "Expiry (YYYY-MM-DD)", txtExp);

        JButton btnOk = new JButton(edit ? "Save" : "Add");
        JButton btnCancel = new JButton("Cancel");
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBtns.add(btnOk); pBtns.add(btnCancel);
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2; d.add(pBtns, gc);

        // Prefill for edit
        if (edit) {
            int found = -1;
            for (int i=0; i<model.getRowCount(); i++) {
                if ((int) model.getValueAt(i,0) == productId) { found = i; break; }
            }
            if (found >= 0) {
                txtName.setText(String.valueOf(model.getValueAt(found,1)));
                cmbCat.setSelectedItem(String.valueOf(model.getValueAt(found,2)));
                txtQty.setText(String.valueOf(model.getValueAt(found,3)));
                txtCost.setText(String.valueOf(model.getValueAt(found,4)));
                txtExp.setText(String.valueOf(model.getValueAt(found,5)));
            }
        }

        btnOk.addActionListener(ev -> {
            try {
                String name = txtName.getText().trim();
                String catName = (String) cmbCat.getSelectedItem();
                int catId = CategoryController.getAll().stream()
                        .filter(c -> c.getName().equals(catName))
                        .findFirst().map(Category::getId).orElse(0);
                int qty = Integer.parseInt(z(txtQty.getText()));
                double cost = Double.parseDouble(z(txtCost.getText()));
                LocalDate exp = txtExp.getText().trim().isEmpty() ? null : LocalDate.parse(txtExp.getText().trim());

                Product p = new Product(edit ? productId : 0, name, catId, catName, qty, cost, exp, emp.getId());
                if (edit) ProductController.update(p);
                else ProductController.add(p);

                refreshProducts();
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Save failed: " + ex.getMessage());
            }
        });
        btnCancel.addActionListener(ev -> d.dispose());

        d.setVisible(true);
    }

    private static void addRow(JDialog d, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.anchor = GridBagConstraints.WEST;
        d.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1; gc.anchor = GridBagConstraints.CENTER;
        d.add(field, gc);
    }
}
