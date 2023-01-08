package tbc.lexer;

import tbc.enums.TokenType;

public record Token(int id, int row, int column, TokenType type, Lexeme lexeme) {

    @Override
    public String toString() {
        return "<%s|id=%d|r=%d|c=%d|'%s'>".formatted(type, id, row, column, lexeme.stringValue());
    }

}
