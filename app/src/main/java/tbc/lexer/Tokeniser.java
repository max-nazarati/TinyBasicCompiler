package tbc.lexer;

import java.util.List;
import java.util.regex.Pattern;

public class Tokeniser {

    private Tokeniser() {

    }

    public static Token<?> tokeniseLine(Line line) {
        if (Pattern.compile("(^\\d+ .*[^ ]$)|(^\\D+ .*[^ ]$)|(^\\w+$)").matcher(line.contents()).matches()) {
            return new LineToken(line.row(), 0, line.contents());
        } else {
            throw new RuntimeException("LINE PARSING FAILED");
        }
    }

    public static List<KeywordToken> tokeniseKeywords(Token<String> line) {
        return null;
    }

}
