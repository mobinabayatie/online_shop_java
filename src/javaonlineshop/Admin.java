package javaonlineshop;

public class Admin extends Account {
    private int id;

    public Admin(int id, String username, String password) {
        super(username, password);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
