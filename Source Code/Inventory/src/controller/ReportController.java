package controller;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class ReportController {
	// List all employees and admin
	public static java.util.List<model.User> listEmployees() {
	    java.util.List<model.User> list = new java.util.ArrayList<>();
	    String sql = "SELECT id, fullname, username, role FROM users " +
	                 "WHERE role IN ('EMPLOYEE','ADMIN') ORDER BY fullname";
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            model.User u = new model.User(
	                rs.getInt("id"),
	                rs.getString("fullname"),
	                rs.getString("username"),
	                rs.getString("role")
	            );
	            list.add(u);
	        }
	    } catch (Exception ignored) {}
	    return list;
	}

   //Monthly Report
    public static DefaultTableModel monthlySummary(int year, int month, Integer employeeId) {
        String[] cols = {"Month", "Items IN", "Items OUT",
                "Stock IN (qty)", "Stock OUT (qty)", "Cash OUT (₹)", "Cash IN (₹)"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        final String base =
                "SELECT DATE_FORMAT(sm.created_at, '%Y-%m') AS ym,\n" +
                "       GROUP_CONCAT(DISTINCT CASE WHEN sm.direction='IN'  THEN p.name END SEPARATOR ', ')  AS items_in,\n" +
                "       GROUP_CONCAT(DISTINCT CASE WHEN sm.direction='OUT' THEN p.name END SEPARATOR ', ')  AS items_out,\n" +
                "       SUM(CASE WHEN sm.direction='IN'  THEN sm.quantity ELSE 0 END)                         AS in_qty,\n" +
                "       SUM(CASE WHEN sm.direction='OUT' THEN sm.quantity ELSE 0 END)                         AS out_qty,\n" +
                "       ROUND(SUM(CASE WHEN sm.direction='IN'  THEN sm.quantity*COALESCE(sm.unit_cost,0)\n" +
                "                      ELSE 0 END),2)                                                        AS cash_out,\n" +
                "       ROUND(SUM(CASE WHEN sm.direction='OUT' THEN sm.quantity*COALESCE(sm.selling_price,0)\n" +
                "                      ELSE 0 END),2)                                                        AS cash_in\n" +
                "FROM stock_movements sm\n" +
                "JOIN products p ON p.id = sm.product_id\n" +
                "WHERE YEAR(sm.created_at)=? AND MONTH(sm.created_at)=?";

        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        params.add(year); params.add(month);
        if (employeeId != null) {
            sql.append(" AND sm.user_id = ?");
            params.add(employeeId);
        }
        sql.append(" ORDER BY ym");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = bind(con.prepareStatement(sql.toString()), params);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                m.addRow(new Object[]{
                        rs.getString("ym"),
                        nz(rs.getString("items_in")),
                        nz(rs.getString("items_out")),
                        rs.getInt("in_qty"),
                        rs.getInt("out_qty"),
                        rs.getBigDecimal("cash_out"),
                        rs.getBigDecimal("cash_in")
                });
            }
        } catch (Exception e) {
            m.addRow(new Object[]{"ERROR", e.getMessage(), "", "", "", "", ""});
        }
        return m;
    }

    /** Monthly Report filtered by employeeId*/
    public static DefaultTableModel monthlyDetails(int year, int month, Integer employeeId) {
        String[] cols = {"Date/Time", "Direction", "Product", "Qty",
                "Unit Cost (₹)", "Selling Price (₹)", "Value (₹)", "Employee"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        String base =
                "SELECT sm.created_at, sm.direction, p.name AS product, sm.quantity,\n" +
                "       sm.unit_cost, sm.selling_price,\n" +
                "       CASE WHEN sm.direction='IN'\n" +
                "            THEN sm.quantity*COALESCE(sm.unit_cost,0)\n" +
                "            ELSE sm.quantity*COALESCE(sm.selling_price,0) END AS value,\n" +
                "       u.fullname AS employee\n" +
                "FROM stock_movements sm\n" +
                "JOIN products p ON p.id = sm.product_id\n" +
                "LEFT JOIN users u ON u.id = sm.user_id\n" +
                "WHERE YEAR(sm.created_at)=? AND MONTH(sm.created_at)=?";

        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        params.add(year); params.add(month);
        if (employeeId != null) {
            sql.append(" AND sm.user_id = ?");
            params.add(employeeId);
        }
        sql.append(" ORDER BY sm.created_at");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = bind(con.prepareStatement(sql.toString()), params);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                m.addRow(new Object[]{
                        rs.getTimestamp("created_at"),
                        rs.getString("direction"),
                        rs.getString("product"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("unit_cost"),
                        rs.getBigDecimal("selling_price"),
                        rs.getBigDecimal("value"),
                        rs.getString("employee")
                });
            }
        } catch (Exception e) {
            m.addRow(new Object[]{"ERROR", "", "", "", "", "", e.getMessage(), ""});
        }
        return m;
    }

    

    // Yearly summary
    public static DefaultTableModel yearlySummary(int year, Integer employeeId) {
        String[] cols = {"Month", "Items IN", "Items OUT",
                "Stock IN (qty)", "Stock OUT (qty)", "Cash OUT (₹)", "Cash IN (₹)"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        final String base =
                "SELECT DATE_FORMAT(sm.created_at, '%Y-%m') AS ym,\n" +
                "       GROUP_CONCAT(DISTINCT CASE WHEN sm.direction='IN'  THEN p.name END SEPARATOR ', ')  AS items_in,\n" +
                "       GROUP_CONCAT(DISTINCT CASE WHEN sm.direction='OUT' THEN p.name END SEPARATOR ', ')  AS items_out,\n" +
                "       SUM(CASE WHEN sm.direction='IN'  THEN sm.quantity ELSE 0 END)                         AS in_qty,\n" +
                "       SUM(CASE WHEN sm.direction='OUT' THEN sm.quantity ELSE 0 END)                         AS out_qty,\n" +
                "       ROUND(SUM(CASE WHEN sm.direction='IN'  THEN sm.quantity*COALESCE(sm.unit_cost,0)\n" +
                "                      ELSE 0 END),2)                                                        AS cash_out,\n" +
                "       ROUND(SUM(CASE WHEN sm.direction='OUT' THEN sm.quantity*COALESCE(sm.selling_price,0)\n" +
                "                      ELSE 0 END),2)                                                        AS cash_in\n" +
                "FROM stock_movements sm\n" +
                "JOIN products p ON p.id = sm.product_id\n" +
                "WHERE YEAR(sm.created_at)=?";

        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        params.add(year);
        if (employeeId != null) {
            sql.append(" AND sm.user_id = ?");
            params.add(employeeId);
        }
        sql.append(" GROUP BY ym ORDER BY ym");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = bind(con.prepareStatement(sql.toString()), params);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                m.addRow(new Object[]{
                        rs.getString("ym"),
                        nz(rs.getString("items_in")),
                        nz(rs.getString("items_out")),
                        rs.getInt("in_qty"),
                        rs.getInt("out_qty"),
                        rs.getBigDecimal("cash_out"),
                        rs.getBigDecimal("cash_in")
                });
            }
        } catch (Exception e) {
            m.addRow(new Object[]{"ERROR", e.getMessage(), "", "", "", "", ""});
        }
        return m;
    }

    /** Yearly details employee filter. */
    public static DefaultTableModel yearlyDetails(int year, Integer employeeId) {
        String[] cols = {"Date/Time", "Direction", "Product", "Qty",
                "Unit Cost (₹)", "Selling Price (₹)", "Value (₹)", "Employee"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        String base =
                "SELECT sm.created_at, sm.direction, p.name AS product, sm.quantity,\n" +
                "       sm.unit_cost, sm.selling_price,\n" +
                "       CASE WHEN sm.direction='IN'\n" +
                "            THEN sm.quantity*COALESCE(sm.unit_cost,0)\n" +
                "            ELSE sm.quantity*COALESCE(sm.selling_price,0) END AS value,\n" +
                "       u.fullname AS employee\n" +
                "FROM stock_movements sm\n" +
                "JOIN products p ON p.id = sm.product_id\n" +
                "LEFT JOIN users u ON u.id = sm.user_id\n" +
                "WHERE YEAR(sm.created_at)=?";

        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        params.add(year);
        if (employeeId != null) {
            sql.append(" AND sm.user_id = ?");
            params.add(employeeId);
        }
        sql.append(" ORDER BY sm.created_at");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = bind(con.prepareStatement(sql.toString()), params);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                m.addRow(new Object[]{
                        rs.getTimestamp("created_at"),
                        rs.getString("direction"),
                        rs.getString("product"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("unit_cost"),
                        rs.getBigDecimal("selling_price"),
                        rs.getBigDecimal("value"),
                        rs.getString("employee")
                });
            }
        } catch (Exception e) {
            m.addRow(new Object[]{"ERROR", "", "", "", "", "", e.getMessage(), ""});
        }
        return m;
    }



    private static PreparedStatement bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i=0; i<params.size(); i++) ps.setObject(i+1, params.get(i));
        return ps;
    }

    private static String nz(String s){ return (s==null || s.isEmpty()) ? "-" : s; }
}
