package tbc.lexer;

import tbc.lexer.exception.ParsingException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokeniser {

    private Tokeniser() {

    }

    public static Token tokeniseLine(Line line) {
        var indexedLinePattern = "(^\\d+ .*[^ ]$)";
        var nonIndexedLinePattern = "(^\\D+ .*[^ ]$)|(^\\w+$)";
        Matcher indexedMatcher = Pattern.compile(indexedLinePattern).matcher(line.contents());
        Matcher nonIndexedMatcher = Pattern.compile(nonIndexedLinePattern).matcher(line.contents());
        if (indexedMatcher.matches()) {
            return new Token(
                    line.row(),
                    Integer.parseInt(line.contents().substring(0, line.contents().indexOf(' '))),
                    0,
                    line.contents(),
                    TokenType.LINE
            );
        } else {
            if (nonIndexedMatcher.matches()) {
                return new Token(line.row(), 0, line.contents(), TokenType.LINE);

            } else {
                throw new RuntimeException(ParsingException.LINE_NOT_PARSABLE.errorMessage().formatted(line.row()));
            }
        }
    }

    public static List<Token> tokeniseKeywords(Token line) {
        int firstWhitespace = line.value().indexOf(' ');
        firstWhitespace = firstWhitespace == -1 ? line.value().length() : firstWhitespace;

        Keyword keyword = Keyword.valueOf(line.value().substring(0, firstWhitespace));
        return switch (keyword) {
            case PRINT, GOTO, INPUT, LET, GOSUB -> {
                String restOfLine = line.value().substring(keyword.getName().length());
                // TODO add string skipping logic "PRINT" is a false positive
                if (someOtherKeyword(restOfLine)) {
                    throw new RuntimeException(ParsingException.UNEXPECTED_KEYWORD_FOUND.errorMessage()
                            .formatted(line.row(), keyword.getName().length() + 1));
                }
                yield List.of(
                        new Token(line.row(), 0, line.value().substring(0, 5), TokenType.KEYWORD),
                        new Token(line.row(), 5, line.value().substring(5), TokenType.BLOB)
                );
            }
            case IF, THEN -> {
                int thenIndex = line.value().lastIndexOf("THEN");
                if (someOtherKeyword(line.value().substring(3, thenIndex))) {
                    throw new RuntimeException(ParsingException.UNEXPECTED_KEYWORD_FOUND.errorMessage()
                            .formatted(line.row(), keyword.getName().length() + 1));
                }
                yield List.of(
                        new Token(line.row(), 0, Keyword.IF.getName(), TokenType.KEYWORD),
                        new Token(line.row(), 2, line.value().substring(2, thenIndex), TokenType.BLOB),
                        new Token(line.row(), thenIndex, Keyword.THEN.getName(), TokenType.KEYWORD),
                        new Token(line.row(), thenIndex + 4, line.value().substring(thenIndex + 4), TokenType.BLOB)
                );
            }
            case RETURN, CLEAR, LIST, RUN, END -> {
                if (!keyword.getName().equals(line.value())) {
                    throw new RuntimeException(ParsingException.TEXT_AFTER_PARAMETERLESS_KEYWORD.errorMessage().formatted(line.row(), 0));
                }
                yield List.of(new Token(line.row(), 0, keyword.getName(), TokenType.KEYWORD));
            }
        };
    }

    private static boolean someOtherKeyword(String restOfLine) {
        return Keyword.names().anyMatch(restOfLine::contains);
    }

}
