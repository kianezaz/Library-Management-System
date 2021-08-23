package librarymanagementsystem;

public abstract class Account {
    
    private String username;
    private String password;
    private Person user;
    
    public Account(String username, String password, Person user) {
        this.username = username;
        this.password = password;
        this.user = user;
    }
    
    public boolean login() {
        DBConnection db = new DBConnection();
        return db.verifyLogin(this.username, this.password);
    }
    
}
