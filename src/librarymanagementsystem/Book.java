package librarymanagementsystem;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Book {
    
    private static final AtomicInteger count = new AtomicInteger(0);
    private int id;
    private String title;
    private String author;
    private int numPages;
    private String genre;
    
    public Book(int id, String title, String primaryAuthor, String genre) {
        this.id = count.getAndIncrement();
        this.title = title;
        this.author = primaryAuthor;
        this.genre = genre;
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
    
    public int getID() {
        return this.id;
    }
}
