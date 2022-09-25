package tbc;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class App {

    public static void main(String[] args) {
        try {
            FileReader fileReader = new FileReader(args[0]);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file reader exception!");
        }
    }
}
