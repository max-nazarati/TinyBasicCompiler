package tbc.lexer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tokeniser {

    private Tokeniser() {

    }

    public static Token<?> tokeniseLine(Line line) {
        var linePattern = "(^\\d+ .*[^ ]$)|(^\\D+ .*[^ ]$)|(^\\w+$)";
        if (Pattern.compile(linePattern).matcher(line.contents()).matches()) {
            return new LineToken(line.row(), 0, line.contents());
        } else {
            throw new RuntimeException("LINE PARSING FAILED");
        }
    }

    public static List<KeywordToken> tokeniseKeywords(Token<String> line) {
        return null;
    }

}
