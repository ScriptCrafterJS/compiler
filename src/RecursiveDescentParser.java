import java.util.*;

public class RecursiveDescentParser {
    private List<String> tokens;
    private int currentTokenIndex = 0;
    private String currentToken;

    // the constructor accepts the list of tokens
    public RecursiveDescentParser(List<String> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(currentTokenIndex);
    }

    private void getToken() {
        currentTokenIndex++;
        // maintain the current token so we dont go out of bounds
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            currentToken = null;
        }
    }

    // error() method to handle syntax errors that may occur during the parsing
    // process
    private void error(String message) {
        System.err.println("Error: " + message + " provided " + currentToken);
        // System.err.println("Error: " + message + " provided " + currentToken + "
        // (index " + currentTokenIndex + ")");
        System.exit(1);
    }

    private boolean match(String token) {
        if (currentToken != null && currentToken.equals(token)) {
            getToken();
            return true;
        }
        return false;
    }

    private void program() {
        libDecl();
        declarations();
        while (currentToken != null && currentToken.equals("function")) {
            functionDecl();
        }
        block();
        // here we expect the last token to be 'exit' if it doesn't match we throw an
        // error
        if (!match("exit")) {
            error("Expected 'exit'");
        }
    }

    // libDecl() method to parse the library declarations
    private void libDecl() {
        if (currentToken != null && currentToken.equals("#include")) {
            getToken();
            if (!match("<"))
                error("Expected '<'");
            if (!isValidFileName(currentToken))
                error("Invalid file name");
            if (!match(">"))
                error("Expected '>'");
            if (!match(";"))
                error("Expected ';'");
            libDecl();
        } else if (currentToken == null) {
            return;
        } else {
            error("Unexpected token: " + currentToken);
        }
    }

    private boolean isValidFileName(String fileName) {
        if (fileName.isEmpty() || fileName == null) {
            return false;
        }

        // check if the file name has a valid extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return false; // no extension found
        }

        // check if the file name contains only valid characters
        String regex = "^[a-zA-Z0-9_\\-\\.]+$";
        if (!fileName.matches(regex)) {
            return false;
        }
        getToken();
        return true;
    }

    private void declarations() {
        constDecl();
        varDecl();
    }

    private void constDecl() {
        if (match("const")) {
            dataType();
            if (!match("const-name")) {
                error("Expected 'const-name'");
            }
            if (!match("=")) {
                error("Expected '='");
            }
            value();
            if (!match(";")) {
                error("Expected ';'");
            }
            constDecl();
        }
    }

    private void value() {
        if (currentToken != null && currentToken.matches("\\d+\\.\\d+")) { // Real number pattern
            realValue();
        } else if (currentToken != null && currentToken.matches("\\d+")) { // Integer pattern
            integerValue();
        } else {
            error("Expected a numeric value (integer or real)");
        }
    }

    private void integerValue() {
        if (currentToken != null && currentToken.matches("\\d+")) {
            getToken();
        } else {
            error("Expected an integer value");
        }
    }

    private void realValue() {
        if (currentToken != null && currentToken.matches("\\d+\\.\\d+")) {
            getToken();
        } else {
            error("Expected a real value");
        }
    }

    private boolean isValidConstName(String constName) {
        if (constName.isEmpty() || constName == null) {
            return false;
        }

        // check if the const name contains only valid characters
        String regex = "^[a-zA-Z0-9_\\-]+$";
        if (!constName.matches(regex)) {
            return false;
        }

        return true;
    }

    private void varDecl() {
        if (match("var")) {
            dataType();
            nameList();
            if (!match(";")) {
                error("Expected ';'");
            }
            varDecl();
        }
    }

    private void dataType() {
        if (match("int") || match("float") || match("char")) {
            return;
        }
        error("Expected data type (int, float, char)");

    }

    private void nameList() {
        if (!match("var-name"))
            error("Expected 'var-name'");
        moreNames();
    }

    private void functionDecl() {
        functionHeading();
        declarations();
        block();
        if (!match(";"))
            error("Expected ';'");
    }

    private void functionHeading() {
        if (!match("function"))
            error("Expected 'function'");
        if (!match("function-name"))
            error("Expected 'function-name'");
        if (!match(";"))
            error("Expected ';'");
    }

    private void block() {
        if (!match("newb"))
            error("Expected 'newb'");
        stmtList();
        if (!match("endb"))
            error("Expected 'endb'");
    }

    // ! we have to check for the endb condition thing if its the right thing to do
    private void stmtList() {
        while (currentToken != null && !currentToken.equals("endb")) {
            statement();
            if (!match(";"))
                error("Expected ';'");
        }
    }

    private void statement() {
        if (match("var-name")) {
            if (!match(":="))
                error("Expected ':='");
            exp();
        } else if (match("cin")) {
            if (!match(">>"))
                error("Expected '>>'");
            if (!match("var-name"))
                error("Expected 'var-name'");
        } else if (match("cout")) {
            if (!match("<<"))
                error("Expected '<<'");
            nameValue();
        } else if (match("if")) {
            if (!match("("))
                error("Expected '('");
            condition();
            if (!match(")"))
                error("Expected ')'");
            statement();
            elsePart();
        } else if (match("while")) {
            if (!match("("))
                error("Expected '('");
            condition();
            if (!match(")"))
                error("Expected ')'");
            // since the block is the same for the body of the while statement we called it
            block();
        } else if (match("repeat")) {
            stmtList();
            if (!match("until"))
                error("Expected 'until'");
            condition();
        } else if (match("call")) {
            if (!match("function-name"))
                error("Expected 'function-name'");
        } else {
            block();
        }
    }

    private void exp() {
        term();
        expPrime();
    }

    private void expPrime() {
        if (match("+") || match("-")) {
            term();
            expPrime();
        }
    }

    private void term() {
        factor();
        termPrime();
    }

    private void termPrime() {
        if (match("*") || match("/") || match("mod") || match("div")) {
            factor();
            termPrime();
        }
    }

    // ! here we have to fix the check for the value because its a function
    private void factor() {
        if (match("(")) {
            exp();
            if (!match(")")) {
                error("Expected ')'");
            }
        } else if (match("var-name")) {

        } else if (match("const-name")) {

        } else {
            value();
        }
    }

    private void nameValue() {
        if (match("var-name") || match("const-name") || match("value")) {
            return;
        }
        error("Expected 'var-name', 'const-name', or 'value'");
    }

    private void condition() {
        nameValue();
        if (match("=") || match("=!") || match("<") || match("=<") || match(">") || match("=>")) {
            nameValue();
        } else {
            error("Expected '==', '!=', '<', '<=', '>', or '>='");
        }

    }

    private void elsePart() {
        if (match("else")) {
            statement();
        }
    }

    public static void main(String[] args) {
        String input = "#include <file-name>; const int x = 10; var float y; exit";
        Scanner lexer = new Scanner(input);
        lexer.scan();
        List<String> tokens = lexer.getTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        parser.program();
        System.out.println("Parsing completed successfully.");
    }
}