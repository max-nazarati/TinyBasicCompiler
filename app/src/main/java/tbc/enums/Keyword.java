package tbc.enums;

import tbc.lexer.Lexeme;

import java.util.Arrays;
import java.util.List;

public enum Keyword {
    PRINT("PRINT"),
    IF("IF"),
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

    private final Lexeme lexeme;

    public static List<String> lexemes() {
        return Arrays.stream(Keyword.values()).map(x -> x.lexeme.stringValue()).toList();

    }

    private Keyword(String lexeme) {
        this.lexeme = new Lexeme(lexeme);
    }
}
