package librarymanagementsystem;

import java.sql.*;
import java.util.ArrayList;

public class DBConnection {
    
    public Connection myConn;
    public Statement myStmt;
    
    public DBConnection() {
        try {
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");            
        }
        catch (Exception e) {
            System.out.println("Failed to create connection to the library database");
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        try {
            if (this.myConn != null) {
                this.myConn.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Member> searchMembers(String username) {
        Statement st = null;
        ResultSet rs = null;
        ArrayList<Member> members = new ArrayList<Member>();
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("Select member_username FROM Members WHERE member_username LIKE '%" + username + "%'");
            while (rs.next()) {
                Member currMember = new Member(rs.getString(1));
                members.add(currMember);
            }
            return members;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public void deleteAccount(Account account) {
        Statement st = null;
        try {
            st = this.myConn.createStatement();
            if (account instanceof Member) {
                st.executeUpdate("DELETE FROM Members WHERE member_username = '" + account.getUsername() + "'");
            }
            else {
                st.executeUpdate("DELETE FROM Librarians WHERE librarian_username = '" + account.getUsername() + "'");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public boolean removeBook(int bookId) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT book_num_checked_out FROM Books WHERE book_id = " + bookId);
            if (!rs.next()) {
                System.out.println("Failed to find book with the given book id\n");
                return false;
            }
            if (rs.getInt(1) > 0) {
                System.out.println("Failed to remove book. At least one copy is currently checked out");
                return false;
            }
            st.executeUpdate("DELETE FROM Books WHERE book_id = " + bookId);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public int bookExactLookup(String title, String author, String genre) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT book_id FROM Books WHERE book_title = '" + title + "' AND primary_author_name = '" + author + "' AND book_genre = '" + genre + "'");
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public void addNewBook(String title, String author, String genre, int numCopies) {
        Statement st = null;
        try {
            st = this.myConn.createStatement();
            st.executeUpdate("INSERT INTO Books(book_title, primary_author_name, book_genre, book_num_available, "
                    + "book_num_checked_out) VALUES('" + title + "', '" + author + "', '" + genre + "', "
                            + numCopies + ", 0)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public void addExistingBook(int bookId, int numCopies) {
        Statement st = null;
        try {
            st = this.myConn.createStatement();
            st.executeUpdate("UPDATE Books SET book_num_available = book_num_available + " + numCopies + " WHERE book_id = " + bookId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
     
    public void updateFine(Member member, double newFine) {
        Statement st = null;
        try {
            st = this.myConn.createStatement();
            st.executeUpdate("UPDATE Members SET member_fine = " + newFine + " WHERE member_username = '" + member.getUsername() + "'");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public void returnBook(Member member, BookItem book) {
        Statement st = null;
        try {
            st = this.myConn.createStatement();
            st.executeUpdate("DELETE FROM Books_Members WHERE member_username = '" + member.getUsername() + "'"
                    + " AND book_id = " + book.getID());
            st.executeUpdate("UPDATE Books SET book_num_available = book_num_available + 1, book_num_checked_out = book_num_checked_out - 1 WHERE book_id = " + book.getID());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public ArrayList<BookItem> getMemberBooks(String username) {
        Statement st = null;
        ResultSet rs = null;
        ArrayList<BookItem> memberBooks = new ArrayList<BookItem>();
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT book_id, book_due_date, renewed FROM Books_Members WHERE member_username = '" + username + "'");
            Book currBook;
            while (rs.next()) {
                currBook = this.searchBooksByID(rs.getInt(1));
                java.sql.Date dueTime = rs.getDate(2);
                Boolean renewed = rs.getBoolean(3);
                BookItem currBookItem = new BookItem(currBook.getID(), currBook.getTitle(),
                        currBook.getAuthor(), currBook.getGenre(), dueTime, renewed);
                memberBooks.add(currBookItem);
            }
            return memberBooks;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public void renewMemberBook(Member member, Book book) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT book_due_date FROM Books_Members WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            if (rs.next()) {
                long currDueDate = rs.getDate(1).getTime();
                System.out.println("The current date is " + rs.getDate(1));
                java.sql.Date newDueDate = new java.sql.Date(currDueDate + (14 * 86400000));
                System.out.println("The new due date is " + newDueDate + "\n");
                st.executeUpdate("UPDATE Books_Members SET book_due_date = '" + newDueDate + "', renewed = TRUE WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public Account verifyLogin(String username, String password, String accountType) {
        if (!accountType.equals("Librarians") && !accountType.equals("Members")) {
            return null;
        }
        Statement st = null;
        ResultSet rs = null;
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        Account account = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT * FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "' AND " + singularAccType + "_password = '" + password + "'");
            if (rs.next()) {
                Person user = new Person(rs.getString(3), rs.getString(4));
                if (accountType.equals("Members")) {
                    ArrayList<BookItem> memberBooks = getMemberBooks(username);
                    account = new Member(rs.getString(1), rs.getString(2), user, memberBooks, rs.getInt(5));
                }
                else {
                    account = new Librarian(rs.getString(1), rs.getString(2), user);
                }
            }
            return account;
        }
        catch (Exception e) {
            System.out.println("Failed to verify login");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public boolean usernameAvailable(String username, String accountType) {
        Statement st = null;
        ResultSet rs = null;
        boolean available = false;
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT 1 FROM " + accountType + " WHERE " + singularAccType + "_username = '" + username + "'");
            if (!rs.next()) {
                available = true;
            }
            return available;
        }
        catch (Exception e) {
            System.out.println("Failed to verify login");
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    
    public void createAccount(String username, String password, String accountType, Person user) {
        Statement st = null;
        String singularAccType = accountType.substring(0, accountType.length() - 1);
        try {
            st = this.myConn.createStatement();
            st.executeUpdate("INSERT INTO " + accountType + "(" + singularAccType + "_username, " + singularAccType + "_password, "
                    + singularAccType + "_name, " + singularAccType + "_email) VALUES('" + username + "', '" + password + "', '" 
                    + user.getName() + "', '" + user.getEmail() + "')");
        }
        catch (Exception e) {
            System.out.println("Failed to create account");
            e.printStackTrace();
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public ArrayList<Book> searchBooksByTitle(String title) {
        Statement st = null;
        ResultSet rs = null;
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT * FROM Books WHERE book_title LIKE '%" + title + "%'");
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
            return books;
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of any book containing title '" + title + "'");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public ArrayList<Book> searchBooksByAuthor(String author) {
        Statement st = null;
        ResultSet rs = null;
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT * FROM Books WHERE primary_author_name LIKE '%" + author +"%'");
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
            return books;
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of any book with author '" + author + "'");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public ArrayList<Book> searchBooksByGenre(String genre) {
        Statement st = null;
        ResultSet rs = null;
        ArrayList<Book> books = new ArrayList<Book>();
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT * FROM Books WHERE book_genre LIKE '%" + genre + "%'");
            while (rs.next()) {
                Book currBook = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
                books.add(currBook);
            }
            return books;
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with genre '" + genre + "'");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public Book searchBooksByID(int id) {
        Statement st = null;
        ResultSet rs = null;
        Book book = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT * FROM Books WHERE book_id = " + id);
            rs.next();
            book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            return book;
        }
        catch (Exception e) {
            System.out.println("Failed to check availability of book with id " + id);
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public boolean bookAvailable(Book book) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT 1 FROM Books WHERE book_id = " + book.getID() + " AND book_num_available > 0");
            if (rs.next()) {
                return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    public boolean userDoesNotHaveBook(Member member, Book book) {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = this.myConn.createStatement();
            rs = st.executeQuery("SELECT 1 FROM Books_Members WHERE member_username = '" + member.getUsername() + "' AND book_id = " + book.getID());
            if (!rs.next()) {
                return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
    
    public BookItem checkoutBook(Member member, Book book) {
        Statement st = null;
        long dueTimeMilliseconds = System.currentTimeMillis() + (14 * 86400000);
        java.sql.Date dueTime = new Date(dueTimeMilliseconds);
        BookItem bookItem = new BookItem(book, dueTime, false);
        try {
            st = this.myConn.createStatement();
            st.executeUpdate("UPDATE Books SET book_num_available = book_num_available - 1, book_num_checked_out = book_num_checked_out + 1 WHERE Book_id = " + book.getID());
            st.executeUpdate("INSERT INTO Books_Members VALUES('" + member.getUsername() + "', " + book.getID() + ", '" + dueTime + "', FALSE)");
            return bookItem;
        }
        catch (Exception e) {
            System.out.println("Failed to checkout book");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (Exception e) {}
        }
    }
    
}