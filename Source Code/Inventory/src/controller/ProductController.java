
package controller;

import model.Product;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    private static Product mapRow(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name")); 
        p.setQuantity(rs.getInt("quantity"));
        p.setUnitCost(rs.getDouble("unit_cost"));

        Date d = rs.getDate("expiry_date");
        p.setExpiryDate(d == null ? null : d.toLocalDate());

        p.setAddedByUserId(rs.getInt("added_by")); 
        return p;
    }

   
    public static List<Product> getAllWithStatus() {
        String sql =
            "SELECT p.id, p.name, p.category_id, c.name AS category_name, " +
            "       p.quantity, p.unit_cost, p.expiry_date, p.added_by " +
            "FROM products p " +
            "LEFT JOIN categories c ON c.id = p.category_id " +
            "ORDER BY p.id";
        List<Product> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public static List<Product> getAllWithCategory() {
        return getAllWithStatus();
    }

    /** delete only rows that truly expired*/
    public static int deleteExpired() {
        String sql = "DELETE FROM products WHERE expiry_date IS NOT NULL AND expiry_date < CURDATE()";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    	
    //add product
    public static void add(model.Product p) throws Exception {
        String sql = "INSERT INTO products(name, category_id, quantity, unit_cost, expiry_date, added_by) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getUnitCost());
            if (p.getExpiryDate() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, java.sql.Date.valueOf(p.getExpiryDate()));
            ps.setInt(6, p.getAddedByUserId());
            ps.executeUpdate();
        }
    }
    //update product
    public static void update(model.Product p) throws Exception {
        String sql = "UPDATE products SET name=?, category_id=?, quantity=?, unit_cost=?, expiry_date=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getQuantity());
            ps.setDouble(4, p.getUnitCost());
            if (p.getExpiryDate() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, java.sql.Date.valueOf(p.getExpiryDate()));
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }
    //delete product
    public static void delete(int id) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    	//stock IN
    public static boolean stockIn(int productId, int qty, double unitCost, int userId) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement up = con.prepareStatement("UPDATE products SET quantity = quantity + ? WHERE id=?");
                 PreparedStatement log = con.prepareStatement(
                        "INSERT INTO stock_movements(product_id, user_id, direction, quantity, unit_cost, reason) " +
                        "VALUES (?,?,?,?,?, 'in')")) {
                up.setInt(1, qty); up.setInt(2, productId); up.executeUpdate();
                log.setInt(1, productId); log.setInt(2, userId); log.setString(3, "IN");
                log.setInt(4, qty); log.setDouble(5, unitCost); log.executeUpdate();
            }
            con.commit();
            return true;
        }
    }
    	//stock OUT
    public static boolean stockOut(int productId, int qty, int userId, String reason, Double sellingPrice) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            
            try (PreparedStatement ck = con.prepareStatement(
                    "SELECT quantity FROM products WHERE id=? FOR UPDATE")) {
                ck.setInt(1, productId);
                ResultSet rs = ck.executeQuery();
                if (!rs.next() || rs.getInt(1) < qty) throw new Exception("Not enough stock");
            }

            
            try (PreparedStatement upd = con.prepareStatement(
                    "UPDATE products SET quantity = quantity - ? WHERE id=?")) {
                upd.setInt(1, qty);
                upd.setInt(2, productId);
                upd.executeUpdate();
            }

            // log movement with selling price
            try (PreparedStatement ins = con.prepareStatement(
                    "INSERT INTO stock_movements(product_id, direction, quantity, unit_cost, selling_price, reason, user_id) " +
                    "VALUES (?,?,?,NULL,?,?,?)")) {
                ins.setInt(1, productId);
                ins.setString(2, "OUT");
                ins.setInt(3, qty);
                ins.setObject(4, sellingPrice);     // can be null if you truly don’t track revenue
                ins.setString(5, reason);
                ins.setInt(6, userId);
                ins.executeUpdate();
            }

            con.commit();
            return true;
        }
    }

    public static void stockOut(int productId, int qty, int userId, String reason) throws Exception {
        stockOut(productId, qty, userId, reason, 0.0); 
    }


    public static List<String> findExpiringSoon(int days) {
        String sql = "SELECT name FROM products WHERE expiry_date IS NOT NULL AND expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        List<String> names = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) names.add(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return names;
    }

    public static List<String> findZeroStock() {
        String sql = "SELECT name FROM products WHERE quantity <= 0";
        List<String> names = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) names.add(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return names;
    }

    public static List<String> findLowStock(int threshold) {
        String sql = "SELECT name FROM products WHERE quantity > 0 AND quantity < ?";
        List<String> names = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) names.add(rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return names;
    }
}
