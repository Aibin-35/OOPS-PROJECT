package view;

import controller.ReportController;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

/** A reusable dialog for Monthly / Yearly reports with Employee filter. */
public class ReportDialog extends JDialog {

    public enum Mode { MONTHLY, YEARLY }

    // Header controls
    private final Mode mode;
    private final JComboBox<Integer> cmbYear = new JComboBox<>();
    private final JComboBox<Integer> cmbMonth = new JComboBox<>();
    private final JComboBox<Object>  cmbEmployee = new JComboBox<>();

    private final JTabbedPane tabs = new JTabbedPane();
    private final JTable tblSummary = new JTable();
    private final JTable tblDetails = new JTable();

    public ReportDialog(Frame owner, Mode mode) {
        super(owner, mode == Mode.MONTHLY ? "Monthly Report" : "Yearly Report", true);
        this.mode = mode;
        setSize(900, 520);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        add(header(), BorderLayout.NORTH);
        add(center(), BorderLayout.CENTER);
        add(footer(), BorderLayout.SOUTH);

        populateSelectors();
        runNow(); // first load
    }

    /* UI blocks */

    private JPanel header() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.add(new JLabel("Year:"));
        p.add(cmbYear);
        p.add(new JLabel("Month:"));
        p.add(cmbMonth);
        p.add(new JLabel("Employee:"));
        p.add(cmbEmployee);

        if (mode == Mode.YEARLY) {
            cmbMonth.setEnabled(false);
        }
        return p;
    }

    private JComponent center() {
        // Summary
        tblSummary.setModel(new DefaultTableModel(
                new Object[]{"Month","Items IN","Items OUT","Stock IN (qty)","Stock OUT (qty)","Cash OUT (₹)","Cash IN (₹)"}, 0));
        JScrollPane sp1 = new JScrollPane(tblSummary);
        tabs.addTab("Summary", sp1);

        // Details
        tblDetails.setModel(new DefaultTableModel(
                new Object[]{"Date/Time","Direction","Product","Qty","Unit Cost (₹)","Selling Price (₹)","Value (₹)","Employee"}, 0));
        JScrollPane sp2 = new JScrollPane(tblDetails);
        tabs.addTab("Details", sp2);

        return tabs;
    }

    private JPanel footer() {
        JButton btnRun = new JButton("Run");
        btnRun.addActionListener(e -> runNow());
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(btnRun); p.add(btnClose);
        return p;
    }

    /*  Data loaders  */

    private void populateSelectors() {
        // Year (±6 around current)
        int y = java.time.LocalDate.now().getYear();
        for (int yy = y - 6; yy <= y + 1; yy++) cmbYear.addItem(yy);
        cmbYear.setSelectedItem(y);

        // Month 1..12
        for (int m = 1; m <= 12; m++) cmbMonth.addItem(m);
        cmbMonth.setSelectedItem(java.time.LocalDate.now().getMonthValue());

        // Employees: first "All Employees" then actual users
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        model.addElement("All Employees");
        List<User> users = ReportController.listEmployees();
        for (User u : users) model.addElement(u);   // relies on User.toString()
        cmbEmployee.setModel(model);
    }

    private void runNow() {
        int year = (Integer) cmbYear.getSelectedItem();
        Integer month = cmbMonth.isEnabled() ? (Integer) cmbMonth.getSelectedItem() : null;
        Integer empId = null;

        Object sel = cmbEmployee.getSelectedItem();
        if (sel instanceof User) empId = ((User) sel).getId();

        if (mode == Mode.MONTHLY) {
            loadMonthly(year, month, empId);
        } else {
            loadYearly(year, empId);
        }
    }

    private void loadMonthly(int year, int month, Integer empId) {
        // Summary
        DefaultTableModel sum = ReportController.monthlySummary(year, month, empId);
        // If the controller returned 0 rows, add a row with the header month (feel friendly)
        if (sum.getRowCount() == 0) {
            YearMonth ym = YearMonth.of(year, month);
            sum.addRow(new Object[]{ym.toString(), "-", "-", 0, 0, 0.0, 0.0});
        }
        tblSummary.setModel(sum);

        // Details
        DefaultTableModel det = ReportController.monthlyDetails(year, month, empId);
        tblDetails.setModel(det);
        autoResize(tblSummary);
        autoResize(tblDetails);
        tblSummary.setAutoCreateRowSorter(true);
        tblDetails.setAutoCreateRowSorter(true);

    }

    private void loadYearly(int year, Integer empId) {
        // Summary (one row per month)
        DefaultTableModel sum = ReportController.yearlySummary(year, empId);
        if (sum.getRowCount() == 0) {
            sum.addRow(new Object[]{year + "-01", "-", "-", 0, 0, 0.0, 0.0});
        }
        tblSummary.setModel(sum);

        // Details (all year)
        DefaultTableModel det = ReportController.yearlyDetails(year, empId);
        tblDetails.setModel(det);
        autoResize(tblSummary);
        autoResize(tblDetails);
        tblSummary.setAutoCreateRowSorter(true);
        tblDetails.setAutoCreateRowSorter(true);

    }

    private void autoResize(JTable t) {
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int c=0; c<t.getColumnCount(); c++) {
            int w = 120;
            for (int r=0; r<t.getRowCount(); r++) {
                Object val = t.getValueAt(r,c);
                Component comp = t.getDefaultRenderer(t.getColumnClass(c))
                                  .getTableCellRendererComponent(t, val, false, false, r, c);
                w = Math.max(w, comp.getPreferredSize().width + 18);
            }
            t.getColumnModel().getColumn(c).setPreferredWidth(w);
        }
        t.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }
}
