package tbc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class App {

    public static void main(String[] args) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));
            List<String> lineStrings = fileReader.lines().toList();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("FILE READER EXCEPTION");
        }
    }
}
