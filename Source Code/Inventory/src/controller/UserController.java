
package controller;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    public static List<User> listEmployees() {
        String q = "SELECT id, fullname, username, role FROM users WHERE role='EMPLOYEE' ORDER BY fullname";
        List<User> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("fullname"),
                    rs.getString("username"),
                    rs.getString("role")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static boolean usernameExists(String username) {
        String q = "SELECT 1 FROM users WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { return true; }
    }

    public static boolean addEmployee(String fullname, String username, String password) {
        String q = "INSERT INTO users(fullname, username, password, role) VALUES (?,?,?,'EMPLOYEE')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setString(1, fullname);
            ps.setString(2, username);
            ps.setString(3, password);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteEmployee(int id) {
        String q = "DELETE FROM users WHERE id=? AND role='EMPLOYEE'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
