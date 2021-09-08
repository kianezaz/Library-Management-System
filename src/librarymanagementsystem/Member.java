package librarymanagementsystem;

import java.sql.Date;
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
             System.out.println("You currently have this book checked out\n");
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
        book.setRenewed(true);
        java.sql.Date newDueTime = new java.sql.Date(book.getDueTime().getTime() + (14 * 86400000));
        book.setDueTime(newDueTime);
    }
    
    public void returnBook(BookItem book) {
        DBConnection db = new DBConnection();
        db.returnBook(this, book);
        this.booksCheckedOut.remove(book);
    }
    
    public void updateFineUponLogin() {
        DBConnection db = new DBConnection();
        double fineIncrement = 0;
        for (int i = 0; i < booksCheckedOut.size(); i++) {
            int daysOverdue = (int) ((System.currentTimeMillis() - booksCheckedOut.get(i).getDueTime().getTime()) / 86400000);
            if (daysOverdue <= 0) {
                continue;
            }
            fineIncrement += .25 * daysOverdue;
        }
        if (fineIncrement > 0) {
            this.fine += fineIncrement;
            db.updateFine(this, fine);
        }
    }
    
    public void payFine(double amount) {
        DBConnection db = new DBConnection();
        db.updateFine(this, this.getFine() - amount);
        this.fine -= amount;
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
