package mehdi.bad.addons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KuudraUtils {

    public static boolean matchesFreshRegex(String str) {
        String regex = "Party > .* (\\w+): FRESH(?:ED.*)?";
        return str.matches(regex);
    }

    // Function to extract the [name] from the string
    public static String extractName(String str) {
        String regex = "Party > .* (\\w+): FRESH(?:ED.*)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

}
