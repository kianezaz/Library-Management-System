package librarymanagementsystem;

import java.sql.Timestamp;

public class BookItem extends Book {
    
    Timestamp dueTime;
    boolean renewed;
    
    public BookItem(Book book, Timestamp dueTime, boolean renewed) {
        super(book.getID(), book.getTitle(), book.getAuthor(), book.getGenre());
        this.dueTime = dueTime;
        this.renewed = renewed;
    }
    
    public BookItem(int id, String title, String primaryAuthor, String genre, Timestamp dueTime, boolean renewed) {
        super(id, title, primaryAuthor, genre);
        this.dueTime = dueTime;
        this.renewed = renewed;
    }
    
    public Timestamp getDueTime() {
        return this.dueTime;
    }
    
    public boolean getRenewed() {
        return this.renewed;
    }
}
