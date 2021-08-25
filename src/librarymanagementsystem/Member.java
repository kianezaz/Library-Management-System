package librarymanagementsystem;

import java.util.ArrayList;
import java.util.Map;

public class Member extends Account {
    
    private Map<Book, Integer> booksCheckedOut;
    private int fine;
    
    public Member(String username, String password, Person user, int fine) {
        super(username, password, user);
        this.fine = fine;
    }
    
    public boolean doesNotHaveBook(Book book) {
        if (book == null) {
            return false;
        }
        DBConnection db = new DBConnection();
        if (db.userDoesNotHaveBook(this, book)) {
            return true;
        }
        System.out.println("You currently have this book checked out");
        return false;
    }
    
    public void checkoutBook(Book book) {
        DBConnection db = new DBConnection();
        db.checkoutBook(this, book);
    }
    
}
