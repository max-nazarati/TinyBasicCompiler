package tbc.lexer;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TokenStream {
    private List<? extends Token<?>> tokens;
    private int currentIndex = 0;

    public TokenStream(Stream<String> lines) {
        tokens = lines.map(this::tokeniseLine).toList();
    }

    public List<? extends Token<?>> getTokens() {
        return tokens;
    }
    private LineToken tokeniseLine(String line) {
        int row = 0;
        if (Pattern.compile("(^\\d+ .*[^ ]$)|(^\\D+ .*[^ ]$)|(^\\w+$)").matcher(line).matches()) {
            return new LineToken(row, 0, line);
        } else {
            throw new RuntimeException("LINE PARSING FAILED");
        }
    }

}
