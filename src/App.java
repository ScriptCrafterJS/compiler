import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.awt.FileDialog;
import java.awt.Frame;

public class App {
    public static void main(String[] args) {
        String input = openFile();
        if (input == null) {
            System.out.println("No file selected or an error occurred.");
            return;
        }
        Scanner lexer = new Scanner(input);
        lexer.scan();
        List<String> tokens = lexer.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(i + ": " + tokens.get(i));
        }
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        parser.program();
        System.out.println("Parsing completed successfully.");
    }

    private static String openFile() {
        FileDialog fileDialog = new FileDialog((Frame) null, "Select a file", FileDialog.LOAD);
        fileDialog.setFile("*.txt");
        fileDialog.setVisible(true);
        String filePath = fileDialog.getDirectory() + fileDialog.getFile();
        if (filePath != null) {
            File selectedFile = new File(filePath);
            try {
                return new String(Files.readAllBytes(selectedFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}