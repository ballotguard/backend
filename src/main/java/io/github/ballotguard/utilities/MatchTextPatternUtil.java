package io.github.ballotguard.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchTextPatternUtil {
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
