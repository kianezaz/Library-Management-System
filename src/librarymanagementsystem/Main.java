package librarymanagementsystem;

import java.util.Scanner;

public class Main {
    
    private final String badInput = "Invalid option.";
    
    public static void printIdentityOptions() {
        System.out.println("Type 1 if you are a member.");
        System.out.println("Type 2 if you are a librarian.");
        System.out.println("Type 3 if you would like to create a member account.");
        System.out.println("Type 4 if you would like to create a librarian account.");
        System.out.println("Type 5 to exit");
    }
    
    public static boolean validNumInput(int input, int min, int max) { 
        if (input >= min && input <= max) {
            return true;
        }
        return false;
    }
    
    public static boolean endProgram(int input, int exitVal) {
        if (input == exitVal) {
            return true;
        }
        return false;
    }
        
    
    public static void main(String[] args) {
        // Library myLib = new Library();
        int option;
        Scanner sc = new Scanner(System.in);
        DBConnection db = new DBConnection();
        System.out.println("Welcome to the Library Management System!");
        do {
            printIdentityOptions();
            option = sc.nextInt();
            sc.nextLine();
        } while (!validNumInput(option, 1, 5));
        if (endProgram(option, 5)) {
            return;
        }
        String accountType;
        if (option == 1 || option == 2) {
            if (option == 1) {
                accountType = "Members";
            }
            else {
                accountType = "Librarians";
            }
            do {
                System.out.println("Enter username: ");
                String username = sc.nextLine();
                System.out.println("Enter password: ");
                String password = sc.nextLine();
                if (db.verifyLogin(username, password, accountType)) {
                    break;
                }
                System.out.println("Incorrect username or password");
            } while (true);
            System.out.println("Verified login");
        }
        else {
            if (option == 3) {
                accountType = "Members";
            }
            else {
                accountType = "Librarians";
            }
            Person newUser;
            System.out.println("Enter your first name: ");
            String firstName = sc.nextLine();
            System.out.println("Enter your last name: ");
            String lastName = sc.nextLine();
            System.out.println("Enter your email: ");
            String email = sc.nextLine();
            System.out.println("Enter your new username: ");
            String username;
            do {
                username = sc.nextLine();
                if (db.usernameAvailable(username, accountType)) {
                    break;
                }
                System.out.println("That username is not available. Please choose another username");
            } while (true);
            System.out.println("Enter your new password: ");
            String password = sc.nextLine();
            newUser = new Person(firstName + " " + lastName, email);
            db.createAccount(username, email, accountType, newUser);
            System.out.println("Account created!");
        }
    }

}
