package javaonlineshop;

public class User extends Account {
    private int id;
    private String email;

    public User(int id, String username, String password, String email) {
        super(username, password);
        this.id = id;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
