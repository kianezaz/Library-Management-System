package librarymanagementsystem;

import java.util.ArrayList;
import java.util.Map;

public class Member extends Account {
    
    private Map<Book, Integer> booksCheckedOut;
    private int fine;
    
    public Member(String username, String password, Person user) {
        super(username, password, user);
    }
    
    
    public boolean checkAvailabilityByTitle(String title) {
        DBConnection db = new DBConnection();
        ArrayList<Book> books = db.searchBooksByTitle(title);
        Book selectedBook = Book.selectBookFromList(books);
        return db.checkBookAvailability(selectedBook.getID());
    }
}
