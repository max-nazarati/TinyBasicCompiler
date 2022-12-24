package tbc.lexer.pipe;

import tbc.lexer.Line;
import tbc.lexer.Token;
import tbc.lexer.TokenType;
import tbc.lexer.exception.ParsingException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record LinePipe(List<Line> lines) implements Pipe {
    private static final PipeState state = PipeState.INIT;

    public TokenPipe toTokenPipe() {
        return new TokenPipe(lines.stream().map(this::tokeniseLine));
    }

    public PipeState state() {
        return state;
    }

    private Token tokeniseLine(Line line) {
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

}
