package poe;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== Registration ===");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter South African cell phone number (e.g. +27 066 260 5530): ");
        String cellPhoneNumber = scanner.nextLine().trim();

        String registrationResult = login.registerUser(username, password, cellPhoneNumber, firstName, lastName);
        System.out.println(registrationResult);

        boolean registered = login.checkUserName(username)
                && login.checkPasswordComplexity(password)
                && login.checkCellPhoneNumber(cellPhoneNumber);

        if (!registered) {
            System.out.println("Registration failed. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine().trim();

        boolean loginSuccessful = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loginSuccessful));

        scanner.close();
    }
}
