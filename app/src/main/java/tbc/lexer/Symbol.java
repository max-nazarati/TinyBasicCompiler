package tbc.lexer;

import java.util.List;

public enum Symbol {
    EQ("="),
    LT("<"),
    GT(">"),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    private final String string;

    public String string() {
        return string;
    }

    Symbol(String s) {
        string = s;
    }

    public static boolean isRelational(String s) {
        return EQ.string.equals(s) || LT.string.equals(s) || GT.string.equals(s);
    }

    public static List<String> getRelops() {
        return List.of(EQ.string, LT.string, GT.string);
    }
}
