package tbc;

import tbc.lexer.TokenStream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class App {

    public static void main(String[] args) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));
            new TokenStream(fileReader.lines());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file reader exception!");
        }
    }
}
