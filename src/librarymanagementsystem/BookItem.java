package librarymanagementsystem;


public class BookItem extends Book {
    
    java.sql.Date dueTime;
    boolean renewed;
    
    public BookItem(Book book, java.sql.Date dueTime, boolean renewed) {
        super(book.getID(), book.getTitle(), book.getAuthor(), book.getGenre());
        this.dueTime = dueTime;
        this.renewed = renewed;
    }
    
    public BookItem(int id, String title, String primaryAuthor, String genre, java.sql.Date dueTime, boolean renewed) {
        super(id, title, primaryAuthor, genre);
        this.dueTime = dueTime;
        this.renewed = renewed;
    }
    
    public java.sql.Date getDueTime() {
        return this.dueTime;
    }
    
    public boolean getRenewed() {
        return this.renewed;
    }
}
