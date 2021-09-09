package librarymanagementsystem;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Main {
    
    private static final String badInput = "Invalid option.\n";
    
    public static void printIdentityOptions() {
        int option;
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type 1 if you are a member.");
            System.out.println("Type 2 if you are a librarian.");
            System.out.println("Type 3 if you would like to create a member account.");
            System.out.println("Type 4 if you would like to create a librarian account.");
            System.out.println("Type 5 to exit\n");
            option = sc.nextInt();
            System.out.println();
            if (option == 1) {
                promptLogin(1);
            }
            else if (option == 2) {
                promptLogin(2);
            }
            else if (option == 3) {
                createAccount(3);
            }
            else if (option == 4) {
                createAccount(4);
            }
            else if (option == 5) {
                return;
            }
            else {
                System.out.println(badInput);
            }
        }
    }
    
    public static void createAccount(int accountTypeInt) {
        Scanner sc = new Scanner(System.in);
        DBConnection db = new DBConnection();
        String accountTypeString;
        if (accountTypeInt == 3) {
            accountTypeString = "Members";
        }
        else {
            accountTypeString = "Librarians";
        }
        Person newUser;
        System.out.println("Enter your first name: ");
        String firstName = sc.nextLine();
        System.out.println("Enter your last name: ");
        String lastName = sc.nextLine();
        System.out.println("Enter your email: ");
        String email = sc.nextLine();
        String username;
        do {
            System.out.println("Enter your new username: ");
            username = sc.nextLine();
            if (db.usernameAvailable(username, accountTypeString)) {
                break;
            }
            System.out.println();
            System.out.println("That username is not available. Please choose another username\n");
        } while (true);
        System.out.println("Enter your new password: ");
        String password = sc.nextLine();
        System.out.println();
        newUser = new Person(firstName + " " + lastName, email);
        db.createAccount(username, password, accountTypeString, newUser);
        db.disconnect();
        System.out.println("Account created!\n");
        printIdentityOptions();
    }
    
    public static void promptLogin(int accountTypeInt) {
        Scanner sc = new Scanner(System.in);
        DBConnection db = new DBConnection();
        String accountTypeString;
        if (accountTypeInt == 1) {
            accountTypeString = "Members";
        }
        else {
            accountTypeString = "Librarians";
        }
        Account account;
        while (true) {
            System.out.println("Enter username: ");
            String username = sc.nextLine();
            System.out.println("Enter password: ");
            String password = sc.nextLine();
            System.out.println();
            account = db.verifyLogin(username, password, accountTypeString);
            if (account != null) {
                break;
            }
            System.out.println("Incorrect username or password\n");
        }
        db.disconnect();
        System.out.println("Verified login\n");
        if (accountTypeInt == 1) {
            Member member = (Member) account;
            member.updateFineUponLogin();
            printMemberMainMenu(member);
            return;
        }
        else {
            Librarian librarian = (Librarian) account;
            printLibrarianMainMenu(librarian);
            return;
        }
    }
    
    public static void printLibrarianMainMenu(Librarian librarian) {
        int actionOption;
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type 1 if you would like to add a book to the library catalog");
            System.out.println("Type 2 if you would like to remove a book from the library catalog");
            System.out.println("Type 3 to delete a member's account");
            System.out.println("type 4 to delete your account");
            System.out.println("Type 5 to log out\n");
            actionOption = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (actionOption == 1) {
                addBook(librarian);
            }
            else if (actionOption == 2) {
                removeBook(librarian);
            }
            else if (actionOption == 3) {
                deleteMemberAccount(librarian);
            }
            else if (actionOption == 4) {
                deleteOwnAccount(librarian);
            }
            else if (actionOption == 5) {
                System.exit(0);
            }
            else {
                System.out.println(badInput);
            }
        }
    }
    
    public static void deleteMemberAccount(Librarian librarian) {
        Scanner sc = new Scanner(System.in);
        Member chosenMember = null;
        while (true) {
            chosenMember = searchMember(librarian);
            if (chosenMember == null) {
                System.out.println("Type 'y' if you would like to continue searching");
                System.out.println("Type 'n' if you would like to exit to main menu\n");
                String failedSearchOption = sc.nextLine();
                System.out.println();
                if (failedSearchOption.equals("y") || failedSearchOption.equals("Y")) {
                    continue;
                }
                return;
            }
            break;
        }
        librarian.deleteMemberAccount(chosenMember);
        System.out.println("Successfully deleted account!\n");
    }
    
    public static Member searchMember(Librarian librarian) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Member> searchedMembers = new ArrayList<Member>();
        Member searchedMember = null;
        System.out.println("Enter member's username:");
        String username = sc.nextLine();
        searchedMembers = librarian.searchMembers(username);
        return handleSearchedMembers(searchedMembers);      
    }
    
    public static Member handleSearchedMembers(ArrayList<Member> members) {
        if (members.isEmpty()) {
            System.out.println("\nSorry, no matching members were found in our system\n");
            return null;
        }
        Scanner sc = new Scanner(System.in);
        Member currMember;
        Member chosenMember;
        int memberOption = 0;
        while (true) {
            System.out.println("\nType the number that matches the member to be removed, or 0 if no members match\n");
            for (int i = 1; i <= members.size(); i++) {
                currMember = members.get(i - 1);
                System.out.println(i + "\tUsername: " + currMember.getUsername());
            }
            System.out.println();
            memberOption = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (memberOption > 0 && memberOption <= members.size()) {
                chosenMember = members.get(memberOption - 1);
                break;
            }
            else if (memberOption == 0) {
                return null;
            }
            else {
                System.out.println(badInput);
            }
        }
        return chosenMember;
    }
    
    public static void removeBook(Librarian librarian) {
        Scanner sc = new Scanner(System.in);
        boolean flag = false;
        while (true) {
            System.out.println("Type 1 if you would like to search by title");
            System.out.println("Type 2 if you would like to search by author");
            System.out.println("Type 3 if you would like to search by genre");
            System.out.println("Type 4 if you would like to search by ID\n");
            int searchOption = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (searchOption < 1 || searchOption > 4) {
                System.out.println(badInput);
                continue;
            }
            Book chosenBook = searchBook(searchOption);
            if (chosenBook == null) {
                System.out.println("Type 'y' if you would like to continue searching");
                System.out.println("Type 'n' if you would like to exit to main menu\n");
                String failedSearchOption = sc.nextLine();
                System.out.println();
                if (failedSearchOption.equals("y") || failedSearchOption.equals("Y")) {
                    continue;
                }
                return;
            }
            flag = librarian.removeBook(chosenBook.getID());
            break;
        }
        if (flag) {
            System.out.println("Successfully removed book!\n");
        }
    }
    
    public static void addBook(Librarian librarian) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter book title: ");
        String title = sc.nextLine();
        System.out.println("Enter book author: ");
        String author = sc.nextLine();
        System.out.println("Enter book genre: ");
        String genre = sc.nextLine();
        System.out.println("How many copies of this book would you like to add?");
        int numCopies = sc.nextInt();
        sc.nextLine();
        System.out.println();
        librarian.addBook(title, author, genre, numCopies);
        System.out.println("Successfully added " + numCopies + " copies of this book!\n");
    }
    
    public static void printMemberMainMenu(Member member) {
        int actionOption;
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type 1 if you would like to check out a book");
            System.out.println("Type 2 if you would like to renew a checked out book");
            System.out.println("Type 3 if you would like to see the list of books you currently have checked out");
            System.out.println("Type 4 if you would like to return a checked out book");
            System.out.println("Type 5 if you would like to view and/or pay off your fine");
            System.out.println("Type 6 if you would like to delete your account");
            System.out.println("Type 7 to log out\n");
            actionOption = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (actionOption == 1) {
                checkoutProcess(member);
            } 
            else if (actionOption == 2) {
                renewProcess(member);
            }
            else if (actionOption == 3) {
                listBooks(member);
            }
            else if (actionOption == 4) {
                returnBook(member);
            }
            else if (actionOption == 5) {
                viewFine(member);
            }
            else if (actionOption == 6) {
                deleteOwnAccount(member);
            }
            else if (actionOption == 7) {
                return;
            }
            else {
                System.out.println(badInput);
            }
        }
    }
    
    public static void deleteOwnAccount(Account account) {
        account.deleteOwnAccount();
        System.out.println("Successfully deleted account!\n");
    }
    
    public static void viewFine(Member member) {
        String option;
        Scanner sc = new Scanner(System.in);
        System.out.println("Your total fine is " + member.getFine() + "\n");
        if (member.getFine() == 0) {
            return;
        }
        while (true) {
            System.out.println("Type y if you would like to pay off some of or all of this fine");
            System.out.println("Type n to exit to the main menu\n");
            option = sc.nextLine();
            if (option.equals("Y") || option.equals("y") || option.equals("N") || option.equals("n")) {
                break;
            }
            System.out.println(badInput);
        }
        if (option.equals("Y") || option.equals("y")) {
            payFine(member);
        }
    }
    
    public static void payFine(Member member) {
        double amount;
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type in the amount you would like to pay");
            amount = sc.nextDouble();
            System.out.println();
            if (amount < 0) {
                System.out.println("Cannot pay a negative amount\n");
            }
            else if (amount > member.getFine()) {
                System.out.println("Cannot pay an amount greater than your current fine\n");
            }
            else {
                break;
            }
        }
        member.payFine(amount);
        System.out.println("Payment successfully made!\n");
    }
    
    // Think about implementing this by allowing member to return multiple books at once   
    public static void returnBook(Member member) {
        if (member.getBooksCheckedOut().size() == 0) {
            return;
        }
        Scanner sc = new Scanner(System.in);
        listBooks(member);
        int bookNum = 0;
        while (true) {
            System.out.println("Enter the number corresponding to the book you would like to return");
            bookNum = sc.nextInt();
            System.out.println();
            if (bookNum < 1 || bookNum > member.getBooksCheckedOut().size()) {
                System.out.println(badInput);
                continue;
            }
            break;
        }
        BookItem bookToReturn = member.getBooksCheckedOut().get(bookNum - 1);
        member.returnBook(bookToReturn);
        java.sql.Date currDate = new java.sql.Date(System.currentTimeMillis());
        // book is overdue
        if (currDate.after(bookToReturn.getDueTime())) {
            long differenceMilliseconds = currDate.getTime() - bookToReturn.getDueTime().getTime();
            int daysOverdue = (int) (differenceMilliseconds / 86400000);
            System.out.println("This book is " + daysOverdue + " days overdue\n");
        }
        System.out.println("Successfully returned book!\n");
    }
    
    public static void listBooks(Member member) {
        if (member.getBooksCheckedOut().size() == 0) {
            System.out.println("You currently do not have any books checked out\n");
        }
        else {
            BookItem curr;
            for (int i = 0; i < member.getBooksCheckedOut().size(); i++) {
                curr = member.getBooksCheckedOut().get(i);
                System.out.println((i + 1) + ":\tTitle: " + curr.getTitle() + " | Author: " + curr.getAuthor()
                    + " | Genre: " + curr.getGenre() + " | Renewed: " + curr.getRenewed() + " | "
                    + "Due time: " + curr.getDueTime());
            }
            System.out.println();
        }
    }
    
 // Think about implementing this by allowing member to renew multiple books at once 
    public static void renewProcess(Member member) {
        Scanner sc = new Scanner(System.in);
        BookItem curr;
        ArrayList<BookItem> allowedToRenew = new ArrayList<BookItem>();
        ArrayList<BookItem> memberBooks = member.getBooksCheckedOut();
        for (int i = 0, j = 0; i < memberBooks.size(); i++) {
            curr = memberBooks.get(i);
            if (!curr.getRenewed()) {
                System.out.println((j + 1) + "\tTitle: " + curr.getTitle() + 
                        " | Author: " + curr.getAuthor() + " | Genre: " + curr.getGenre());
                allowedToRenew.add(curr);
                j++;
            }
        }
        System.out.println();
        if (allowedToRenew.isEmpty()) {
            System.out.println("No books to renew\n");
            return;
        }
        int bookNum;
        while (true) {
            System.out.println("Type the number that matches the book you would like to renew (you may only renew each book once)\n");
            bookNum = sc.nextInt();
            System.out.println();
            if (bookNum < 1 || bookNum > allowedToRenew.size()) {
                System.out.println(badInput);
                continue;
            }
            break;
        }
        BookItem bookToRenew = allowedToRenew.get(bookNum - 1);
        member.renewBook(bookToRenew);
        System.out.println("Successfully renewed selected book!\n");
    }
    
    public static void checkoutProcess(Member member) {
        if (member.getBooksCheckedOut().size() == 10) {
            System.out.println("You may not have more than 10 books checked out at a time."
                    + " Return 1 or more books before checking out another book");
        }
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type 1 if you would like to search by title");
            System.out.println("Type 2 if you would like to search by author");
            System.out.println("Type 3 if you would like to search by genre\n");
            int searchOption = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (searchOption < 1 || searchOption > 3) {
                System.out.println(badInput);
                continue;
            }
            Book chosenBook = searchBook(searchOption);
            if (chosenBook == null || !chosenBook.isAvailable() || !member.doesNotHaveBook(chosenBook)) {
                System.out.println("Type 'y' if you would like to continue searching");
                System.out.println("Type 'n' if you would like to exit to main menu\n");
                String failedSearchOption = sc.nextLine();
                System.out.println();
                if (failedSearchOption.equals("y") || failedSearchOption.equals("Y")) {
                    continue;
                }
                return;
            }
            member.checkoutBook(chosenBook);
            break;
        }
        System.out.println("Successfully checked out book!\n");
        return;
    }
    
    public static Book searchBook(int searchOption) {
        Scanner sc = new Scanner(System.in);
        DBConnection db = new DBConnection();
        ArrayList<Book> books;
        Book book;
        if (searchOption == 1) {
            System.out.println("Enter book title:");
            String title = sc.nextLine();
            books = db.searchBooksByTitle(title);
            book = handleSearchedBooks(books);
        }
        else if (searchOption == 2) {
            System.out.println("Enter author:");
            String author = sc.nextLine();
            books = db.searchBooksByAuthor(author);
            book = handleSearchedBooks(books);
        }
        else if (searchOption == 3) {
            System.out.println("Enter genre:");
            String genre = sc.nextLine();
            books = db.searchBooksByGenre(genre);
            book = handleSearchedBooks(books);
        }
        else {
            System.out.println("Enter book ID:");
            int bookId = sc.nextInt();
            sc.nextLine();
            book = db.searchBooksByID(bookId);
        }
        db.disconnect();
        return book;
    }
    
    public static Book handleSearchedBooks(ArrayList<Book> books) {
        if (books.isEmpty()) {
            System.out.println("\nSorry, no matching books were found in our catalog\n");
            return null;
        }
        Scanner sc = new Scanner(System.in);
        Book currBook;
        Book chosenBook;
        int bookOption = 0;
        while (true) {
            System.out.println("\nType the number that matches the book you're interested in, or 0 if no books match\n");
            for (int i = 1; i <= books.size(); i++) {
                currBook = books.get(i - 1);
                System.out.println(i + "\tTitle: " + currBook.getTitle() + " | Author: " + currBook.getAuthor() + " | Genre: " + currBook.getGenre());
            }
            System.out.println();
            bookOption = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (bookOption > 0 && bookOption <= books.size()) {
                chosenBook = books.get(bookOption - 1);
                break;
            }
            else if (bookOption == 0) {
                return null;
            }
            else {
                System.out.println(badInput);
            }
        }
        return chosenBook;
    }
        
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Library Management System!");
        printIdentityOptions();
    }

}