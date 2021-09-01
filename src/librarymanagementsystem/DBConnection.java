package librarymanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBConnection {
    
    public Connection myConn;
    public Statement myStmt;
    
    public DBConnection() {
        try {
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
            //myStmt = myConn.createStatement();
            
        }
        catch (Exception e) {
            System.out.println("Failed to create connection to the library database");
            e.printStackTrace();
        }
    }
    
    public void returnBook(Member member, BookItem book) {
        try {
            Statement st = this.myConn.createStatement();
            st.executeUpdate("DELETE FROM Books_Members WHERE member_username = '" + member.getUsername() + "'"
                    + " AND book_id = " + book.getID());
            st.executeUpdate("UPDATE Books SET book_num_available = book_num_available + 1, book_num_checked_out = book_num_checked_out - 1 WHERE book_id = " + book.getID());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<BookItem> getMemberBooks(String username) {
        ArrayList<BookItem> memberBooks = new ArrayList<BookItem>();
        try {
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT book_id, book_due_date, renewed FROM Books_Members WHERE member_username = '" + username + "'");
            Book currBook;
            while (rs.next()) {
                currBook = this.searchBooksByID(rs.getInt(1));
                //int currID = rs.getInt(1);
                java.sql.Date dueTime = rs.getDate(2);
                Boolean renewed = rs.getBoolean(3);
                BookItem currBookItem = new BookItem(currBook.getID(), currBook.getTitle(),
                        currBook.getAuthor(), currBook.getGenre(), dueTime, renewed);
                memberBooks.add(currBookItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return memberBooks;
    }
    
    public void renewMemberBook(Member member, Book book) {
        try {
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT book_due_date FROM Books_Members WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            if (rs.next()) {
                long currDueDate = rs.getDate(1).getTime();
                java.sql.Date newDueDate = new java.sql.Date(currDueDate + (14 * 86400000));
                st.executeUpdate("UPDATE Books_Members SET book_due_date = '" + newDueDate + "', renewed = TRUE WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Account verifyLogin(String username, String password, String accountType) {
        if (!accountType.equals("Librarians") && !accountType.equals("Members")) {
            return null;
        }
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        boolean correctLogin = false;
        Account account;
        try {
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "' AND " + singularAccType + "_password = '" + password + "'");
            if (rs.next()) {
                Person user = new Person(rs.getString(3), rs.getString(4));
                if (accountType.equals("Members")) {
                    ArrayList<BookItem> memberBooks = getMemberBooks(username);
                    account = new Member(rs.getString(1), rs.getString(2), user, memberBooks, rs.getInt(5));
                }
                else {
                    account = new Librarian(rs.getString(1), rs.getString(2), user);
                }
                return account;
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
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 1 FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "'");
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
            Statement st = this.myConn.createStatement();
            st.executeUpdate("INSERT INTO " + accountType + "(" + singularAccType + "_username, " + singularAccType + "_password, "
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
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Books WHERE book_title = '" + title + "'");
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
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Books WHERE primary_author_name = '" + author +"'");
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
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Books WHERE genre = '" + genre + "'");
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
    
    public Book searchBooksByID(int id) {
        Book book = null;
        try {
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Books WHERE book_id = " + id);
            rs.next();
            book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with id " + id);
            e.printStackTrace();
        }
        return book;
    }
    
    public boolean bookAvailable(Book book) {
        try {
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 1 FROM Books WHERE book_id = " + book.getID() + " AND book_num_available > 0");
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
            Statement st = this.myConn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 1 FROM Books_Members WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            if (!rs.next()) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public BookItem checkoutBook(Member member, Book book) {
        long dueTimeMilliseconds = System.currentTimeMillis() + (14 * 86400000);
        java.sql.Date dueTime = new Date(dueTimeMilliseconds);
        BookItem bookItem = new BookItem(book, dueTime, false);
        try {
            Statement st = this.myConn.createStatement();
            st.executeUpdate("UPDATE Books SET book_num_available = book_num_available - 1, book_num_checked_out = book_num_checked_out + 1 WHERE Book_id = " + book.getID());
            st.executeUpdate("INSERT INTO Books_Members VALUES('" + member.getUsername() + "', " + book.getID() + ", '" + dueTime + "', FALSE)");
        }
        catch (Exception e) {
            System.out.println("Failed to checkout book");
            e.printStackTrace();
        }
        return bookItem;
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
