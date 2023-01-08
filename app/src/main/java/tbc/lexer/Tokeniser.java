package tbc.lexer;

import tbc.enums.Keyword;
import tbc.enums.TokenType;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record Tokeniser(List<List<Character>> lines, List<Token> tokens) {

    public Tokeniser(BufferedReader reader) {
        this(toLinesOfChars(reader), List.of());
    }

    private static List<List<Character>> toLinesOfChars(BufferedReader textBuffer) {
        return textBuffer.lines().map(l -> l.chars().mapToObj(c -> (char) c).toList()).toList();
    }

    public Tokeniser tokenise() {
        var tempTokens = new ArrayList<Token>();
        boolean someChange = true;
        while (someChange) {
            someChange = false;
            int id = 0;
            for (int row = 0; row < lines.size(); row++) {
                for (int column = 0; column < lines.get(row).size(); column++) {
                    List<Character> line = lines.get(row);
                    Character c = line.get(column);
                    List<Character> restOfLine = line.subList(column + 1, line.size());

                    if (Character.isWhitespace(c)) {
                        tempTokens.add(new Token(id, row, column, TokenType.WHITESPACE, new Lexeme(c)));

                    } else if (Character.isDigit(c)) {
                        String acc = c + restOfLine.stream()
                                .takeWhile(Character::isDigit)
                                .map(Object::toString)
                                .collect(Collectors.joining());
                        if (!isNextCharWhitespace(column, acc, line)) {
                            throw new RuntimeException("[%d : %d] failed parsing number, only integers are supported".formatted(
                                    row,
                                    column
                            ));
                        }
                        tempTokens.add(new Token(id, row, column, TokenType.NUMBER, new Lexeme(acc)));
                        column += acc.length() - 1;

                    } else if (Character.isUpperCase(c)) {
                        String acc = c + restOfLine.stream()
                                .takeWhile(Character::isUpperCase)
                                .map(Object::toString)
                                .collect(Collectors.joining());
                        if (!isNextCharWhitespace(column, acc, line)) {
                            throw new RuntimeException("[%d : %d] unexpected char after KEYWORD/VARIABLE".formatted(
                                    row,
                                    column
                            ));
                        }
                        TokenType type = Keyword.lexemes().contains(acc) ? TokenType.KEYWORD : TokenType.VARIABLE;
                        tempTokens.add(new Token(id, row, column, type, new Lexeme(acc)));
                        column += acc.length() - 1;

                    } else if (c == '"') {
                        int closingQuotation = restOfLine.indexOf('"');
                        if (closingQuotation == -1) {
                            throw new RuntimeException("[%d : %d] could not find a matching closing quotation mark".formatted(row, column));
                        }
                        String s = restOfLine.subList(0, closingQuotation).stream().map(Object::toString).collect(Collectors.joining());
                        tempTokens.add(new Token(id, row, column, TokenType.STRING, new Lexeme(s)));
                        column += closingQuotation + 1;
                    } else {
                        throw new RuntimeException("[%d : %d] unexpected character".formatted(row, column));
                    }

                    id++;
                }
            }
        }
        return new Tokeniser(lines, Collections.unmodifiableList(tempTokens));
    }

    private boolean isNextCharWhitespace(int column, String acc, List<Character> line) {
        if (acc.length() == 1) {
            return column == line.size() - 1 || Character.isWhitespace(line.get(column + 1));
        }

        return Character.isWhitespace(line.get(column + acc.length()));
    }

}
