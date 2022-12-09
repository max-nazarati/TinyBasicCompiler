package tbc.lexer;

import tbc.lexer.exception.ParsingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        final int keywordEndIndex = keyword.getName().length();
        return switch (keyword) {
            case PRINT, GOTO, INPUT, LET, GOSUB -> {
                String restOfLine = line.value().substring(keyword.getName().length());
                // TODO add string skipping logic "PRINT" is a false positive
                if (someOtherKeyword(restOfLine)) {
                    throw new RuntimeException(ParsingException.UNEXPECTED_KEYWORD_FOUND.errorMessage()
                            .formatted(line.row(), keywordEndIndex));
                }
                yield List.of(
                        new Token(line.row(), 0, line.value().substring(0, keywordEndIndex), TokenType.KEYWORD),
                        new Token(line.row(), keywordEndIndex, line.value().substring(keywordEndIndex), TokenType.BLOB)
                );
            }
            case IF, THEN -> {
                int thenIndex = line.value().lastIndexOf("THEN");
                if (someOtherKeyword(line.value().substring(3, thenIndex))) {
                    throw new RuntimeException(ParsingException.UNEXPECTED_KEYWORD_FOUND.errorMessage()
                            .formatted(line.row(), thenIndex));
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

    public static List<Token> resolveBlobs(List<Token> tokensWithBlobs) {
        var tokensWithoutBlobs = new ArrayList<Token>();
        Token previousToken = tokensWithBlobs.get(0);

        for (Token t : tokensWithBlobs) {
            if (t.tokenType().equals(TokenType.BLOB)) {
                List<Token> distilledBlob = resolveBlob(previousToken, t);
                tokensWithoutBlobs.addAll(distilledBlob);
            } else {
                tokensWithoutBlobs.add(t);
                previousToken = t;
            }
        }

        return tokensWithoutBlobs;
    }

    private static List<Token> resolveBlob(Token previousToken, Token t) {
        if (previousToken.value().equals("IF")) {
            int relopIndex = indexOfRelop(t.value()).orElseThrow(() -> new RuntimeException("could not parse IF body"));
            var relopString = t.value().substring(relopIndex, relopIndex + 1);
            var parts = List.of(t.value().substring(0, relopIndex).trim(), t.value().substring(relopIndex + 1).trim());
            var relop = new Token(t.row(), t.column() + relopIndex, relopString, TokenType.RELOP);
            var left = new Token(t.row(), t.column() + 1, parts.get(0), TokenType.EXPRESSION);
            var right = new Token(t.row(), t.column() + relopIndex + 2, parts.get(1), TokenType.EXPRESSION);

            return List.of(left, relop, right);
        } else if (previousToken.value().equals("THEN")) {
            return List.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.STATEMENT));
        } else if (previousToken.value().equals("GOTO") || previousToken.value().equals("GOSUB")) {
            return List.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.EXPRESSION));
        } else if (previousToken.value().equals("INPUT")) {
            return List.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.VAR_LST));
        } else if (previousToken.value().equals("PRINT")) {
            return List.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.EXPR_LST));
        } else if (previousToken.value().equals("LET")) {
            if (!t.value().contains(" = ")) {
                throw new RuntimeException("this does not look like a correct assignment");
            }
            int assignmentIndex = t.value().indexOf("=");
            var left = new Token(t.row(), t.column() + 1, t.value().substring(0, assignmentIndex).trim(), TokenType.VAR);
            var right = new Token(
                    t.row(),
                    t.column() + assignmentIndex + 2,
                    t.value().substring(assignmentIndex + 1).trim(),
                    TokenType.EXPRESSION
            );
            var assignment = new Token(t.row(), t.column() + assignmentIndex, "=", TokenType.ASSIGNMENT);
            return List.of(left, assignment, right);
        } else {
            throw new RuntimeException("sth went wrong while parsing blobs");
        }

    }

    private static Optional<Integer> indexOfRelop(String s) {
        return Symbol.getRelops().stream()
                .filter(x -> s.contains(" " + x + " "))
                .map(s::indexOf)
                .findFirst();
    }

    private static boolean someOtherKeyword(String restOfLine) {
        return Keyword.names().anyMatch(restOfLine::contains);
    }

}
