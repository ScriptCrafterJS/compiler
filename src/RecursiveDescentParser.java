import java.util.*;

public class RecursiveDescentParser {
    private List<String> tokens;
    private int currentTokenIndex = 0;
    private String currentToken;
    // #include|const|var|int|float|char|function|newb|endb|if|else|while|repeat|until|cin|cout|call|exit
    // create a list full of keywords
    LinkedList<String> keywords = new LinkedList<String>();

    // the constructor accepts the list of tokens
    public RecursiveDescentParser(List<String> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(currentTokenIndex);
        this.keywords.add("#include");
        this.keywords.add("const");
        this.keywords.add("var");
        this.keywords.add("int");
        this.keywords.add("float");
        this.keywords.add("char");
        this.keywords.add("function");
        this.keywords.add("newb");
        this.keywords.add("endb");
        this.keywords.add("if");
        this.keywords.add("else");
        this.keywords.add("while");
        this.keywords.add("repeat");
        this.keywords.add("until");
        this.keywords.add("cin");
        this.keywords.add("cout");
        this.keywords.add("call");
        this.keywords.add("exit");
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
        System.err.println("Error: " + message + " provided " + currentToken + " (index " + currentTokenIndex + ")");
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

    public void program() {
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
        if (match("#include")) {
            if (!match("<"))
                error("Expected '<'");
            if (!isValidFileName(currentToken))
                error("Invalid file name");
            if (!match(">"))
                error("Expected '>'");
            if (!match(";"))
                error("Expected ';'");
            libDecl();
        }
    }

    private boolean isValidFileName(String fileName) {
        if (fileName.isEmpty() || fileName == null) {
            return false;
        }

        // check if the file name has a valid extension
        // int dotIndex = fileName.lastIndexOf('.');
        // if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
        // return false; // no extension found
        // }

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
            name();
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

    private void name() {
        if (currentToken != null && currentToken.matches("[a-zA-Z_][a-zA-Z_0-9-]*|<[^>]+>")) {
            getToken();
        } else {
            error("Expected a name");
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

    // private boolean isValidConstName(String constName) {
    // if (constName.isEmpty() || constName == null) {
    // return false;
    // }

    // // check if the const name contains only valid characters
    // String regex = "^[a-zA-Z0-9_\\-]+$";
    // if (!constName.matches(regex)) {
    // return false;
    // }

    // return true;
    // }

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
        name();
        moreNames();
    }

    private void moreNames() {
        if (match(","))
            nameList();
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
        name();
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

    private void stmtList() {
        if (currentToken.equals("cin") || currentToken.equals("cout") || currentToken.equals("if")
                || currentToken.equals("while") || currentToken.equals("repeat") || currentToken.equals("call")
                || currentToken.equals("newb")
                || (!currentToken.equals("endb") && !currentToken.equals("until")
                        && currentToken.matches("[a-zA-Z_][a-zA-Z_0-9-]*|<[^>]+>"))) {
            statement();
            if (!match(";"))
                error("Expected ';'");
            stmtList();
        }
    }

    private void statement() {
        if (!keywords.contains(currentToken)) {
            assStmt();
        } else if (match("cin")) {
            if (!match(">>"))
                error("Expected '>>'");
            name();
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
            name();
        } else if (currentToken.equals("newb")) {
            block();
        }
    }

    private void assStmt() {
        name();
        if (!match(":="))
            error("Expected ':='");
        exp();
    }

    private void exp() {
        term();
        expPrime();
    }

    private void expPrime() {
        if (match("+") || match("-")) {
            // addOp();
            term();
            expPrime();
        }
    }

    // private void addOp() {
    // if (match("+") || match("-")) {
    // return;
    // } else {
    // error("Expected '+' or '-'");
    // }
    // }

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

    private void factor() {
        if (match("(")) {
            exp();
            if (!match(")")) {
                error("Expected ')'");
            }
        } else if (currentToken != null && currentToken.matches("[a-zA-Z_][a-zA-Z_0-9-]*|<[^>]+>")) {
            name();
        } else {
            value();
        }
    }

    private void nameValue() {
        if (currentToken != null && currentToken.matches("[a-zA-Z_][a-zA-Z_0-9-]*|<[^>]+>")) {
            getToken();
        } else {
            value();
        }
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
}