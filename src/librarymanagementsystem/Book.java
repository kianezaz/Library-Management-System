package librarymanagementsystem;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Book {
    
    private int id;
    private String title;
    private String author;
    private int numPages;
    private String genre;
    
    public Book(int id, String title, String primaryAuthor, String genre) {
        this.id = id;
        this.title = title;
        this.author = primaryAuthor;
        this.genre = genre;
    }
    
    public boolean isAvailable() {
        DBConnection db = new DBConnection();
        if (db.bookAvailable(this)) {
            return true;
        }
        System.out.println("Sorry, all copies of this book are currently checked out");
        return false;
    }
    
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Book)) {
            return false;
        }
        Book other = (Book) obj;
        if (this.id == other.id) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = title.hashCode() + this.author.hashCode();
        return result;
    }
    
    public int getID() {
        return this.id;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getGenre() {
        return this.genre;
    }
}
