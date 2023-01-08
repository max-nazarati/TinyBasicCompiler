package tbc.enums;

import tbc.lexer.Lexeme;

public enum Operator {
    ADD('+'),
    SUB('-'),
    MUL('*'),
    DIV('/');

    private final Lexeme lexeme;

    public Lexeme lexeme() {
        return lexeme;
    }

    private Operator(char lexeme) {
        this.lexeme = new Lexeme(lexeme);
    }
}
