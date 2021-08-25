package librarymanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    
    public Connection myConn;
    public Statement myStmt;
    
    public DBConnection() {
        try {
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
            myStmt = myConn.createStatement();
            
        }
        catch (Exception e) {
            System.out.println("Failed to create connection to the library database");
            e.printStackTrace();
        }
    }
    
    public Member verifyLogin(String username, String password, String accountType) {
        if (!accountType.equals("Librarians") && !accountType.equals("Members")) {
            return null;
        }
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        boolean correctLogin = false;
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "' AND " + singularAccType + "_password = '" + password + "'");
            if (rs.next()) {
                Person user = new Person(rs.getString(3), rs.getString(4));
                Member member = new Member(rs.getString(1), rs.getString(2), user, rs.getInt(5));
                return member;
            }
        }
        catch (Exception e) {
            System.out.println("Failed to verify login");
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean usernameAvailable(String username, String accountType) {
        boolean available = false;
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT 1 FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "'");
            if (!rs.next()) {
                available = true;
            }
        }
        catch (Exception e) {
            System.out.println("Failed to verify login");
            e.printStackTrace();
        }
        return available;
    }
    
    
    public void createAccount(String username, String password, String accountType, Person user) {
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        try {
            // INSERT INTO Members(member_username, member_password, member_name, member_email) VALUES('n', 'pass', 'nader', ne@yahoo.com'
            this.myStmt.executeUpdate("INSERT INTO " + accountType + "(" + singularAccType + "_username, " + singularAccType + "_password, "
                    + singularAccType + "_name, " + singularAccType + "_email) VALUES('" + username + "', '" + password + "', '" 
                    + user.getName() + "', '" + user.getEmail() + "')");
        }
        catch (Exception e) {
            System.out.println("Failed to create account");
            e.printStackTrace();
        }
    }
    
    public ArrayList<Book> searchBooksByTitle(String title) {
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM Books WHERE book_title = '" + title + "'");
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with title " + title);
            e.printStackTrace();
        }
        return books;
    }
    
    public ArrayList<Book> searchBooksByAuthor(String author) {
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM Books WHERE primary_author_name = '" + author +"'");
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with author " + author);
            e.printStackTrace();
        }
        return books;
    }
    
    public ArrayList<Book> searchBooksByGenre(String genre) {
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM Books WHERE genre = '" + genre + "'");
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with genre " + genre);
            e.printStackTrace();
        }
        return books;
    }
    
    public ArrayList<Book> searchBooksByID(int id) {
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM Books WHERE book_id = " + id);
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with id " + id);
            e.printStackTrace();
        }
        return books;
    }
    
    public boolean bookAvailable(Book book) {
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT 1 FROM Books WHERE book_id = " + book.getID() + " AND book_num_available > 0");
            if (rs.next()) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean userDoesNotHaveBook(Member member, Book book) {
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT 1 FROM Books_Members WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            if (!rs.next()) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public void checkoutBook(Member member, Book book) {
        long dueTimeMilliseconds = System.currentTimeMillis() + (14 * 86400000);
        Timestamp dueTime = new Timestamp(dueTimeMilliseconds);
        try {
            this.myStmt.executeUpdate("UPDATE Books SET book_num_available = book_num_available - 1, book_num_checked_out = book_num_checked_out + 1 WHERE Book_id = " + book.getID());
            this.myStmt.executeUpdate("INSERT INTO Books_Members VALUES('" + member.getUsername() + "', " + book.getID() + ", '" + dueTime + "')");
            return;
        }
        catch (Exception e) {
            System.out.println("Failed to checkout book");
            e.printStackTrace();
        }
        return;
    }
    
    
    /*
    
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        try {
            db.myStmt.executeUpdate("INSERT INTO Members(member_username, member_password, "
                    + "member_name, member_email) VALUES('kianezaz', 'pass', 'Kian Ezaz', "
                    + "'kianezaz@yahoo.com')");
        }
        catch(Exception e) {
            System.out.println("Could not insert new member");
            e.printStackTrace();
        }
    }
    */
    
}
