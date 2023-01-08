package tbc.lexer;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record TokenPipe(List<List<Character>> lines, List<Token> tokens) implements Pipe {

    public TokenPipe(BufferedReader reader) {
        this(toLinesOfChars(reader), List.of());
    }

    private static List<List<Character>> toLinesOfChars(BufferedReader textBuffer) {
        return textBuffer.lines().map(l -> l.chars().mapToObj(c -> (char) c).toList()).toList();
    }

    public TokenPipe tokenise() {
        var tempTokens = new ArrayList<Token>();
        boolean someChange = true;
        while (someChange) {
            someChange = false;
            int id = 0;
            for (int row = 0; row < lines.size(); row++) {
                for (int column = 0; column < lines.get(row).size(); column++) {
                    List<Character> line = lines.get(row);
                    Character c = line.get(column);
                    List<Character>  restOfLine = line.subList(column, line.size());

                    if (Character.isWhitespace(c)) {
                        tempTokens.add(new Token(id, row, column, TokenType.WHITESPACE, new Lexeme(c)));

                    } else if (Character.isDigit(c)) {
                        String acc = restOfLine.stream().takeWhile(Character::isDigit).map(Object::toString).collect(Collectors.joining());
                        if (!Character.isWhitespace(line.get(column + acc.length()))) {
                            throw new RuntimeException("[%d : %d] failed parsing number, only integers are supported".formatted(
                                    row,
                                    column
                            ));
                        }
                        tempTokens.add(new Token(id, row, column, TokenType.NUMBER, new Lexeme(acc)));
                        column += acc.length() - 1;

                    } else if (Character.isUpperCase(c)) {
                        String acc = restOfLine.stream()
                                .takeWhile(Character::isUpperCase)
                                .map(Object::toString)
                                .collect(Collectors.joining());
                        if (!Character.isWhitespace(line.get(column + acc.length()))) {
                            throw new RuntimeException("[%d : %d] unexpected char after KEYWORD/VARIABLE".formatted(
                                    row,
                                    column
                            ));
                        }
                        TokenType type = Keyword.lexemes().contains(acc) ? TokenType.KEYWORD : TokenType.VARIABLE;
                        tempTokens.add(new Token(id, row, column, type, new Lexeme(acc)));
                        column += acc.length() - 1;

                    } else {
                        tempTokens.add(new Token(id, row, column, TokenType.BLOB, new Lexeme(c)));
                    }

                    id++;
                }
            }
        }
        return new TokenPipe(lines, Collections.unmodifiableList(tempTokens));
    }
}
