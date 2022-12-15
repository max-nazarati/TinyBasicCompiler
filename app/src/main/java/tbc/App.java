package tbc;

import tbc.lexer.Line;
import tbc.lexer.TopLevelTokeniser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.IntStream;

public class App {

    public static void main(String[] args) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));
            List<String> lineStrings = fileReader.lines().toList();
            List<Line> lines = IntStream.range(0, lineStrings.size()).mapToObj(i -> new Line(i + 1, lineStrings.get(i))).toList();

            lines.stream()
                    .map(TopLevelTokeniser::tokeniseLine).toList();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FILE READER EXCEPTION");
        }
    }
}
