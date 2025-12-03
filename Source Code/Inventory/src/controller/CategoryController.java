
package controller;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryController {
	public static java.util.List<model.Category> getAll() {
	    java.util.List<model.Category> list = new java.util.ArrayList<>();
	    try (var con = DBConnection.getConnection();
	         var st = con.createStatement();
	         var rs = st.executeQuery("SELECT id,name FROM categories ORDER BY name")) {
	        while (rs.next()) list.add(new model.Category(rs.getInt(1), rs.getString(2)));
	    } catch (Exception ignored) {}
	    return list;
	}


    public static boolean add(String name) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO categories(name) VALUES(?)")) {
            ps.setString(1, name);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { return false; }
    }

    public static boolean deleteByName(String name) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM categories WHERE name=?")) {
            ps.setString(1, name);
            return ps.executeUpdate() == 1;
        } catch (Exception e) { return false; }
    }
}
