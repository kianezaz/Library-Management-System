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
                break;
            }
            else if (option == 2) {
                promptLogin(2);
                break;
            }
            else if (option == 3) {
                createAccount(3);
                break;
            }
            else if (option == 4) {
                createAccount(4);
                break;
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
            System.out.println("That username is not available. Please choose another username");
        } while (true);
        System.out.println("Enter your new password: ");
        String password = sc.nextLine();
        newUser = new Person(firstName + " " + lastName, email);
        db.createAccount(username, password, accountTypeString, newUser);
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
        System.out.println("Verified login\n");
        if (accountTypeInt == 1) {
            Member member = (Member) account;
            member.updateFineUponLogin();
            printMemberMainMenu(member);
            return;
        }
        else {
            Librarian librarian = (Librarian) account;
            // printLibrarianMainMenu()
            return;
        }
    }
    
    public static void printMemberMainMenu(Member member) {
        DBConnection db = new DBConnection();
        int actionOption;
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type 1 if you would like to check out a book");
            System.out.println("Type 2 if you would like to renew a checked out book");
            System.out.println("Type 3 if you would like to see the list of books you currently have checked out");
            System.out.println("Type 4 if you would like to return a checked out book");
            System.out.println("Type 5 if you would like to view and/or pay off your fine");
            System.out.println("Type 6 to exit\n");
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
                return;
            }
            else {
                System.out.println(badInput);
            }
        }
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
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type 1 if you would like to search by title");
            System.out.println("Type 2 if you would like to search by author");
            System.out.println("Type 3 if you would like to search by genre\n");
            int searchOption = sc.nextInt();
            sc.nextLine();
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
        System.out.println();
        if (searchOption == 1) {
            System.out.println("Enter book title:");
            String title = sc.nextLine();
            books = db.searchBooksByTitle(title);
        }
        else if (searchOption == 2) {
            System.out.println("Enter author:");
            String author = sc.nextLine();
            books = db.searchBooksByAuthor(author);
        }
        else {
            System.out.println("Enter genre:");
            String genre = sc.nextLine();
            books = db.searchBooksByGenre(genre);
        }
        System.out.println();
        if (books.isEmpty()) {
            System.out.println("Sorry, no matching books were found in our catalog\n");
            return null;
        }
        Book currBook;
        System.out.println("Type the number that matches the book you're interested in\n");
        for (int i = 0; i < books.size(); i++) {
            currBook = books.get(i);
            System.out.println((i + 1) + "\tTitle: " + currBook.getTitle() + " | Author: " + currBook.getAuthor() + " | Genre: " + currBook.getGenre());
        }
        System.out.println();
        int bookOption = sc.nextInt();
        sc.nextLine();
        System.out.println();
        Book chosenBook = books.get(bookOption - 1);
        return chosenBook;
    }
        
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DBConnection db = new DBConnection();
        System.out.println("Welcome to the Library Management System!");
        printIdentityOptions();
    }

}
