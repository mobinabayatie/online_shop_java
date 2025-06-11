package javaonlineshop;

import java.sql.*;

public class DBConnection {
   private static final String URL = "jdbc:mysql://localhost:3306/onlineshop_db";
   private static final String USER = "root";
   private static final String PASSWORD = "";

   public static Connection getConnection() {
       try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           return DriverManager.getConnection(URL, USER, PASSWORD);
       } catch (ClassNotFoundException | SQLException e) {
           e.printStackTrace();
       }
       return null;
   }

   public static boolean validateUser(String username, String password, boolean isAdmin) {
       String query = isAdmin ? "SELECT * FROM admin WHERE username = ? AND password = ?"
                              : "SELECT * FROM users WHERE username = ? AND password = ?";
       try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
           stmt.setString(1, username);
           stmt.setString(2, password);
           try (ResultSet rs = stmt.executeQuery()) {
               return rs.next();
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return false;
   }

   public static int getUserIdByUsername(String username) {
	    String query = "SELECT id FROM users WHERE username = ?";
	    try (Connection conn = getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, username);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("id");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}
   public static User getUserByUsername(String username) {
	    String query = "SELECT * FROM users WHERE username = ?";
	    try (Connection conn = getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, username);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return new User(
	                rs.getInt("id"),
	                rs.getString("username"),
	                rs.getString("password"),
	                rs.getString("email")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public static Admin getAdminByUsername(String username) {
	    String query = "SELECT * FROM admin WHERE username = ?";
	    try (Connection conn = getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, username);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return new Admin(
	                rs.getInt("id"),
	                rs.getString("username"),
	                rs.getString("password")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}


}