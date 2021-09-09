package librarymanagementsystem;

import java.util.ArrayList;

public class Librarian extends Account {
    
    public Librarian(String username, String password, Person user) {
        super(username, password, user);
    }
    
    public void deleteMemberAccount(Member member) {
        DBConnection db = new DBConnection();
        db.deleteAccount(member);
        db.disconnect();
    }
    
    public ArrayList<Member> searchMembers(String username) {
        DBConnection db = new DBConnection();
        ArrayList<Member> members = db.searchMembers(username);
        return members;
    }
    
    public void addBook(String title, String author, String genre, int numCopies) {
        String modifiedTitle = title.toLowerCase();
        String modifiedAuthor = author.toLowerCase();
        String modifiedGenre = genre.toLowerCase();
        DBConnection db = new DBConnection();
        int bookId = db.bookExactLookup(modifiedTitle, modifiedAuthor, modifiedGenre);
        if (bookId != -1) {
            db.addExistingBook(bookId, numCopies);
        }
        else {
            db.addNewBook(modifiedTitle, modifiedAuthor, modifiedGenre, numCopies);
        }
        db.disconnect();
    }
    
    public boolean removeBook(int id) {
        DBConnection db = new DBConnection();
        boolean flag = db.removeBook(id);
        db.disconnect();
        if (flag) {
            return true;
        }
        return false;
    }
    
}
