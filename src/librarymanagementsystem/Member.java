package librarymanagementsystem;

import java.util.ArrayList;
import java.util.HashMap;

public class Member extends Account {
    
    private ArrayList<BookItem> booksCheckedOut;
    private double fine;
    
    public Member(String username, String password, Person user, ArrayList<BookItem> booksCheckedOut, double fine) {
        super(username, password, user);
        this.fine = fine;
        this.booksCheckedOut = booksCheckedOut;
    }
    
    public boolean doesNotHaveBook(Book book) {
        if (book == null) {
            return false;
        }
        if (this.booksCheckedOut.contains(book)) {
             System.out.println("You currently have this book checked out");
             return false;
        }
        return true;
    }
    
    public void checkoutBook(Book book) {
        DBConnection db = new DBConnection();
        BookItem checkedOut = db.checkoutBook(this, book);
        this.booksCheckedOut.add(checkedOut);
    }
    
    public void renewBook(BookItem book) {
        DBConnection db = new DBConnection();
        db.renewMemberBook(this, book);
        int index = this.booksCheckedOut.indexOf(book);
        this.booksCheckedOut.get(index).renewed = true;
    }
    
    public void returnBook(BookItem book) {
        DBConnection db = new DBConnection();
        db.returnBook(this, book);
        this.booksCheckedOut.remove(book);
    }
    
    public void payFine(double amount) {
        DBConnection db = new DBConnection();
        db.updateFine(this, this.getFine() - amount);
    }
    
    public ArrayList<BookItem> getBooksCheckedOut() {
        return this.booksCheckedOut;
    }
    
    public void setBooksCheckedOut(ArrayList<BookItem> books) {
        this.booksCheckedOut = books;
    }
    
    public double getFine() {
        return this.fine;
    }
    
    public void setFine(double fine) {
        this.fine = fine;
        DBConnection db = new DBConnection();
        db.updateFine(this, fine);
    }
}
