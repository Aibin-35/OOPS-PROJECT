package controller;

import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController {

    /** Get all products for employee view */
    public static List<Product> getAllProducts() {
        return ProductController.getAllWithCategory();
    }

    
    public static boolean stockIn(int productId, int qty, double cost, int empId) {
        try {
            ProductController.stockIn(productId, qty, cost, empId); // void call
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Stock OUT 
    public static boolean stockOut(int productId, int qty, int empId, String reason) {
        try {
            ProductController.stockOut(productId, qty, empId, reason); // void call
            return true; // if no exception, success
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean stockOut(int productId, int qty, int empId, String reason, double sellingPrice) {
        try {
            ProductController.stockOut(productId, qty, empId, reason, sellingPrice);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // view stock movements done by this employee 
    public static List<String> getMyStockMovements(int empId) {
        List<String> list = new ArrayList<>();
        String q = "SELECT p.name, sm.direction, sm.quantity, sm.reason, sm.created_at " +
                   "FROM stock_movements sm " +
                   "JOIN products p ON p.id = sm.product_id " +
                   "WHERE sm.user_id=? ORDER BY sm.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(String.format("%s | %s %d (%s) at %s",
                        rs.getString("name"),
                        rs.getString("direction"),
                        rs.getInt("quantity"),
                        rs.getString("reason"),
                        rs.getTimestamp("created_at")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
