package util;

import java.util.regex.Pattern;

/**
 * Lab 04 – Utility class providing static regex-based validation methods.
 * Use {@code import static util.Validator.*;} to call these without the class name.
 */
public final class Validator {

    // Lab 04 – compiled patterns (more efficient than re-compiling each call)
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL_PATTERN  = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PAN_PATTERN    = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern IFSC_PATTERN   = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[1-9][0-9]*$");  // Supplementary

    private Validator() { /* utility class – prevent instantiation */ }

    /**
     * Returns {@code true} if the mobile number is a valid 10-digit Indian mobile number.
     * Must start with 6–9 and contain exactly 10 digits.
     */
    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * Returns {@code true} if the email address is syntactically valid.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Returns {@code true} if the PAN follows the standard format:
     * 5 letters, 4 digits, 1 letter (e.g., ABCDE1234F).
     */
    public static boolean isValidPan(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan).matches();
    }

    /**
     * Returns {@code true} if the IFSC code is valid:
     * 4 alpha chars, '0', 6 alphanumeric chars.
     */
    public static boolean isValidIfsc(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc).matches();
    }

    /**
     * Lab 04 Supplementary – Returns {@code true} if the string represents
     * a positive whole-number amount.
     */
    public static boolean isValidAmount(String amount) {
        return amount != null && AMOUNT_PATTERN.matcher(amount).matches();
    }
}
