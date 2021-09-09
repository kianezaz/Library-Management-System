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
    
    public void deleteAccount() {
        DBConnection db = new DBConnection();
        db.deleteAccount(this);
        db.disconnect();
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public Person getUser() {
        return this.user;
    }
    
}
