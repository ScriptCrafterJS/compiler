import java.util.LinkedList;

public class App {
    public static void main(String[] args) {
        String sourceCode = "#include <file-name>; const int x = 10; var float y;";
        Scanner scanner = new Scanner(sourceCode);
        scanner.scan();
        LinkedList<String> tokens = scanner.getTokens();

        for (String token : tokens) {
            System.out.println(token);
        }
    }
}
