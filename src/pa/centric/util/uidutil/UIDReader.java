package pa.centric.util.uidutil;

import pa.centric.Centric;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UIDReader {
    public static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(Centric.prefix + "Файл не найден: " + e.getMessage());
        }
        return content.toString();
    }
}

