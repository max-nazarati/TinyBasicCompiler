package tbc.lexer;

import java.util.List;
import java.util.regex.Pattern;

public class Tokeniser {

    private Tokeniser() {

    }

    public static Token tokeniseLine(Line line) {
        var linePattern = "(^\\d+ .*[^ ]$)|(^\\D+ .*[^ ]$)|(^\\w+$)";
        if (Pattern.compile(linePattern).matcher(line.contents()).matches()) {
            return new Token(line.row(), 0, line.contents(), TokenType.LINE);
        } else {
            throw new RuntimeException("LINE PARSING FAILED");
        }
    }

    public static List<Token> tokeniseKeywords(Token line) {
        Keyword keyword = Keyword.valueOf(line.value().substring(0, line.value().indexOf(' ')));
        return switch (keyword) {
            case PRINT, GOTO, INPUT, LET, GOSUB -> {
                String restOfLine = line.value().substring(5);
                // TODO add string skipping logic "PRINT" is a false positive
                if (someOtherKeyword(restOfLine)) {
                    throw new RuntimeException("KEYWORD PARSING FAILED %d".formatted(line.row()));
                }
                yield List.of(
                        new Token(line.row(), 0, line.value().substring(0, 5), TokenType.KEYWORD),
                        new Token(line.row(), 5, line.value().substring(5), TokenType.BLOB)
                );
            }
            case IF, THEN -> {
                int thenIndex = line.value().lastIndexOf("THEN");
                if (someOtherKeyword(line.value().substring(3, thenIndex))) {
                    throw new RuntimeException("KEYWORD PARSING FAILED %d".formatted(line.row()));
                }
                yield List.of(
                        new Token(line.row(), 0, Keyword.IF.getName(), TokenType.KEYWORD),
                        new Token(line.row(), 2, line.value().substring(2, thenIndex), TokenType.BLOB),
                        new Token(line.row(), thenIndex, Keyword.THEN.getName(), TokenType.KEYWORD),
                        new Token(line.row(), thenIndex + 4, line.value().substring(thenIndex + 4), TokenType.BLOB)
                );
            }
            case RETURN, CLEAR, LIST, RUN, END -> {
                throw new RuntimeException("NOT IMPLEMENTED");
            }
        };
    }

    private static boolean someOtherKeyword(String restOfLine) {
        return Keyword.names().anyMatch(restOfLine::contains);
    }

}
