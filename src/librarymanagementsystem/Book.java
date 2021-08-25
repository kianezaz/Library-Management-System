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
    
    public static Book selectBookFromList(ArrayList<Book> books) {
        String newLine = System.lineSeparator();
        for (int i = 0; i < books.size(); i++) {
            System.out.println("Enter " + i + " if this is the correct book");
            System.out.println("Title: " + books.get(i).title + " | Author: " + books.get(i).author
                    + " | Genre: " + books.get(i).genre + newLine);
        }
        Scanner sc = new Scanner(System.in);
        int input = sc.nextInt();
        return books.get(input);
    }
    
    //@Override
    public boolean equals(Book book) {
        return false;
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
