package librarymanagementsystem;

public class Librarian extends Account {
    
    public Librarian(String username, String password, Person user) {
        super(username, password, user);
    }
    
    
    public boolean checkoutBook() {
        return false;
    }
    
}
