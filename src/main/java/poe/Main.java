package poe;

import java.util.Scanner;

public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

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

        boolean loginSuccessful = false;
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS && !loginSuccessful; attempt++) {
            System.out.println("\n=== Login ===");
            System.out.print("Enter username: ");
            String loginUsername = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String loginPassword = scanner.nextLine().trim();

            loginSuccessful = login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(loginSuccessful));
        }

        // Only users who have logged in successfully may send messages.
        if (!loginSuccessful) {
            System.out.println("Too many failed login attempts. Exiting.");
            scanner.close();
            return;
        }

        runQuickChat(scanner);
        scanner.close();
    }

    private static void runQuickChat(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");
        int messageLimit = readPositiveInt(scanner, "How many messages would you like to send? ");
        int messagesEntered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int option = readPositiveInt(scanner, "Option: ");

            switch (option) {
                case 1:
                    if (messagesEntered >= messageLimit) {
                        System.out.println("You have already entered all " + messageLimit + " messages.");
                        break;
                    }
                    while (messagesEntered < messageLimit) {
                        enterMessage(scanner, messagesEntered);
                        messagesEntered++;
                    }
                    System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }

    private static void enterMessage(Scanner scanner, int messageNumber) {
        System.out.println("\n--- Message " + (messageNumber + 1) + " ---");

        Message message;
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String recipient = scanner.nextLine().trim();
            System.out.print("Enter your message: ");
            String text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                System.out.println("Please enter a message.");
                continue;
            }
            String lengthResult = Message.checkMessageLength(text);
            if (!lengthResult.equals("Message ready to send.")) {
                System.out.println(lengthResult);
                continue;
            }

            message = new Message(messageNumber, recipient, text);
            String recipientResult = message.checkRecipientCell();
            System.out.println(recipientResult);
            if (recipientResult.equals("Cell phone number successfully captured.")) {
                System.out.println(lengthResult);
                break;
            }
        }

        System.out.println(message.messageIDCreated());
        System.out.println("Message Hash: " + message.getMessageHash());

        System.out.println("What would you like to do with this message?");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int choice = readPositiveInt(scanner, "Choice: ");
        while (choice < Message.OPTION_SEND || choice > Message.OPTION_STORE) {
            System.out.println("Invalid option. Please choose 1, 2 or 3.");
            choice = readPositiveInt(scanner, "Choice: ");
        }

        System.out.println(message.sentMessage(choice));

        if (choice == Message.OPTION_SEND) {
            System.out.println(message.getDetails());
        } else if (choice == Message.OPTION_DISREGARD) {
            String input = scanner.nextLine().trim();
            while (!input.equals("0")) {
                System.out.println("Press 0 to delete the message.");
                input = scanner.nextLine().trim();
            }
            System.out.println("Message deleted.");
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message below
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }
}
