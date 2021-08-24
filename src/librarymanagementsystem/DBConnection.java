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
    
    public boolean verifyLogin(String username, String password, String accountType) {
        if (!accountType.equals("Librarians") && !accountType.equals("Members")) {
            return false;
        }
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        boolean correctLogin = false;
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT 1 FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "' AND " + singularAccType + "_password = '" + password + "'");
            if (rs.next()) {
                correctLogin = true;
            }
        }
        catch (Exception e) {
            System.out.println("Failed to verify login");
            e.printStackTrace();
        }
        return correctLogin;
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
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM Books WHERE book_title = " + title);
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
            ResultSet rs = this.myStmt.executeQuery("SELECT * FROM Books WHERE primary_author_name = " + author);
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
    
    public boolean checkBookAvailability(int id) {
        boolean isAvailable = false;
        try {
            ResultSet rs = this.myStmt.executeQuery("SELECT 1 FROM Books WHERE book_id = " + id + " AND book_numAvailable > 0");
            if (rs.next()) {
                isAvailable = true;
            }
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with id " + id);
            e.printStackTrace();
        }
        return isAvailable;
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
