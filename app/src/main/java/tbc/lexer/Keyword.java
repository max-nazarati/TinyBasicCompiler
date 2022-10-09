package tbc.lexer;

import java.util.Arrays;
import java.util.stream.Stream;

public enum Keyword {
    IF("IF"),
    ELSE("ELSE"),
    PRINT("PRINT"),
    THEN("THEN"),
    GOTO("GOTO"),
    INPUT("INPUT"),
    LET("LET"),
    GOSUB("GOSUB"),
    RETURN("RETURN"),
    CLEAR("CLEAR"),
    LIST("LIST"),
    RUN("RUN"),
    END("END");

    private final String name;

    public String getName() {
        return name;
    }

    public static Stream<String> names() {
        return Arrays.stream(Keyword.values()).map(Keyword::getName);
    }

    Keyword(String name) {
        this.name = name;
    }
}
