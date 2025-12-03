
package controller;

import model.User;
import java.sql.*;

public class AuthController {

   
    public static User login(String username, String password) {
        String u = username == null ? "" : username.trim();
        String p = password == null ? "" : password.trim();

        String sql = "SELECT id, fullname, username, role " +
                     "FROM users WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u);
            ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    
    public static User login(String username, String password, String role) {
        String u = username == null ? "" : username.trim();
        String p = password == null ? "" : password.trim();
        String r = role == null ? "" : role.trim();

        String sql = "SELECT id, fullname, username, role " +
                     "FROM users WHERE username=? AND password=? AND role=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u);
            ps.setString(2, p);
            ps.setString(3, r);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
