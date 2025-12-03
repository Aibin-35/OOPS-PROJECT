package model;

public class User {
    private int id;
    private String fullname;
    private String username;
    private String password;
    private String role;

    //Constructor used by AuthController
    public User(int id, String fullname, String username, String role) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.role = role;
    }

    
    public User() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    @Override
    public String toString() {
        return fullname;
    }

}
