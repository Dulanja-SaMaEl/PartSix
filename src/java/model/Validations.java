package model;

public class Validations {

     public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public static Boolean isPasswordValid(String password) {
        return true;
    }

    public static boolean isDouble(String text) {
        return text.matches("^\\d+(\\.\\d{2})?$");
    }

    public static boolean isInteger(String text) {
        return text.matches("^\\d+$");
    }

    public static boolean isMobileNumberValid(String text) {
        return text.matches("^07[01245678]{1}[0-9]{7}$");
    }
}
