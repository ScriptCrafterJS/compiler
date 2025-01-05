import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {
    private String input;
    private static HashMap<String, String> tokenPatterns = new LinkedHashMap<String, String>();
    private LinkedList<String> tokens = new LinkedList<String>();

    // the scanner constructor accepts the source code as string
    public Scanner(String input) {
        // regex patterns for each token type
        tokenPatterns.put("RESERVED",
                "(#include|const|var|int|float|char|function|newb|endb|if|else|while|repeat|until|cin|cout|call|exit)");
        tokenPatterns.put("SYMBOL", "(;|,|\\(|\\)|>>|<<)");
        tokenPatterns.put("OPERATOR", "(:=|\\+|-|\\*|/|mod|div|=!|=<|=>|<|>|=)");
        tokenPatterns.put("IDENTIFIER", "([a-zA-Z_][a-zA-Z_0-9-]*|<[^>]+>)");
        tokenPatterns.put("REAL", "(\\d+\\.\\d+)");
        tokenPatterns.put("INTEGER", "(\\d+)");
        tokenPatterns.put("WHITESPACE", "(\\s+)");

        this.input = input;
    }

    // the scan method tokenizes the source code
    public void scan() {

        StringBuilder regex = new StringBuilder();

        for (String pattern : tokenPatterns.values()) {
            /*
             * - the result of this will comibne the regulare expressions togethr to form a
             * single regex
             * - this step done to escape the use of nested loops and to make the process
             * faster
             * 
             */
            if (regex.length() > 0) {
                regex.append("|");
            }
            regex.append(pattern);
        }

        // here we use the pattern class to build (compile the regex)
        Pattern pattern = Pattern.compile(regex.toString());
        // the resultant pattern builds the regex pattern to be used in the matcher
        // now we setup the matcher with the input
        Matcher matcher = pattern.matcher(this.input);

        // The matcher.find() iterates through the input and identifies matches based on
        // the regex pattern.
        while (matcher.find()) {
            // the matcher.group() method is used to get the value of the token
            for (String tokenType : tokenPatterns.keySet()) {
                Pattern tokenPattern = Pattern.compile(tokenPatterns.get(tokenType));
                Matcher tokenMatcher = tokenPattern.matcher(matcher.group());
                if (tokenMatcher.matches()) {
                    if (!tokenType.equals("WHITESPACE")) {
                        this.tokens.add(matcher.group());
                    }
                    break;
                }
            }
        }
    }

    public LinkedList<String> getTokens() {
        return this.tokens;
    }
}
