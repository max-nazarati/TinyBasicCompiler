package tbc.lexer.pipe;

import tbc.lexer.Keyword;
import tbc.lexer.Symbol;
import tbc.lexer.Token;
import tbc.lexer.TokenType;
import tbc.lexer.exception.ParsingException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TokenPipe implements Pipe {
    private final Stream<Token> tokens;
    private final PipeState state;

    public TokenPipe(Stream<Token> tokens, Pipe previous) {
        this.tokens = tokens;
        this.state = previous.state().next();
    }

    public TokenPipe(Stream<Token> tokens) {
        this.tokens = tokens;
        this.state = PipeState.INIT;
    }

    public Stream<Token> tokens() {
        return tokens;
    }

    public PipeState state() {
        return state;
    }

    public TokenPipe tokeniseKeywords() {
        stateGuard(PipeState.WITH_LINES);

        return new TokenPipe(tokens.flatMap(this::tokeniseKeywords), this);
    }

    public TokenPipe resolveBlobs() {
        stateGuard(PipeState.WITH_TOP_LVL_KEYWORDS);

        Stream<Token> tokensWithoutBlobs = Stream.empty();
        List<Token> tokenList = this.tokens.toList();
        Token previousToken = tokenList.get(0);

        for (Token t : tokenList) {
            if (t.type().equals(TokenType.BLOB)) {
                Stream<Token> distilledBlob = resolveBlob(previousToken, t);
                tokensWithoutBlobs = Stream.concat(tokensWithoutBlobs, distilledBlob);
            } else {
                tokensWithoutBlobs = Stream.concat(tokensWithoutBlobs, Stream.of(t));
                previousToken = t;
            }
        }

        return new TokenPipe(tokensWithoutBlobs, this);
    }


    private void stateGuard(PipeState expectedState) {
        if (state != expectedState) {
            String className = this.getClass().getName();
            String methodName = className + "#" + Thread.currentThread().getStackTrace()[2].getMethodName();

            throw new RuntimeException("pipeline state and does not allow the invocation of [%s]".formatted(methodName));
        }
    }

    private Stream<Token> resolveBlob(Token previousToken, Token t) {
        Stream<Token> result;
        if (previousToken.value().equals("IF")) {
            int relopIndex = indexOfRelop(t.value()).orElseThrow(() -> new RuntimeException("could not parse IF body"));
            var relopString = t.value().substring(relopIndex, relopIndex + 1);
            var parts = List.of(t.value().substring(0, relopIndex).trim(), t.value().substring(relopIndex + 1).trim());
            var relop = new Token(t.row(), t.column() + relopIndex, relopString, TokenType.RELOP);
            var left = new Token(t.row(), t.column() + 1, parts.get(0), TokenType.EXPRESSION);
            var right = new Token(t.row(), t.column() + relopIndex + 2, parts.get(1), TokenType.EXPRESSION);

            result = Stream.of(relop, left, right);
        } else if (previousToken.value().equals("THEN")) {
            result = Stream.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.STATEMENT));
        } else if (previousToken.value().equals("GOTO") || previousToken.value().equals("GOSUB")) {
            result = Stream.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.EXPRESSION));
        } else if (previousToken.value().equals("INPUT")) {
            result = Stream.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.VAR_LST));
        } else if (previousToken.value().equals("PRINT")) {
            result = Stream.of(new Token(t.row(), t.column() + 1, t.value().trim(), TokenType.EXPR_LST));
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
            result = Stream.of(assignment, left, right);
        } else {
            throw new RuntimeException("sth went wrong while parsing blobs");
        }

        return result;

    }

    private Stream<Token> tokeniseKeywords(Token line) {
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
                yield Stream.of(
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
                yield Stream.of(
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
                yield Stream.of(new Token(line.row(), 0, keyword.getName(), TokenType.KEYWORD));
            }
        };
    }

    private Optional<Integer> indexOfRelop(String s) {
        return Symbol.getRelops().stream()
                .filter(x -> s.contains(" " + x + " "))
                .map(s::indexOf)
                .findFirst();
    }

    private boolean someOtherKeyword(String restOfLine) {
        return Keyword.names().anyMatch(restOfLine::contains);
    }

}