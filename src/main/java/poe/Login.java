package poe;

import java.util.regex.Pattern;

/**
 * Registration and login logic for the POE console application.
 * Regex validation approach referenced from Oracle's official
 * java.util.regex.Pattern documentation:
 * https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
 */
public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    private static final Pattern PASSWORD_CAPITAL = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_NUMBER = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");
    private static final Pattern CELL_NUMBER = Pattern.compile("^\\+27\\d{9,10}$");

    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return PASSWORD_CAPITAL.matcher(password).matches()
                && PASSWORD_NUMBER.matcher(password).matches()
                && PASSWORD_SPECIAL.matcher(password).matches();
    }

    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        String noSpaces = cellPhoneNumber.replace(" ", "");
        return CELL_NUMBER.matcher(noSpaces).matches();
    }

    public String registerUser(String username, String password, String cellPhoneNumber,
                                String firstName, String lastName) {
        boolean validUsername = checkUserName(username);
        boolean validPassword = checkPasswordComplexity(password);
        boolean validCellPhone = checkCellPhoneNumber(cellPhoneNumber);

        if (!validUsername) {
            return "Username is not correctly formatted; please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }
        if (!validPassword) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!validCellPhone) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;

        return "Username successfully captured. Password successfully captured. Cell number successfully captured.";
    }

    public boolean loginUser(String username, String password) {
        return this.username != null
                && this.username.equals(username)
                && this.password != null
                && this.password.equals(password);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
