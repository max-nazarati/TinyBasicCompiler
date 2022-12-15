package tbc.lexer;

import java.util.Objects;

public class Token {
    // TODO: turn into a record?

    private final int row;
    private final int customRow;
    private final int column;
    private final String value;
    private final TokenType type;

    protected Token(int row, int column, String value, TokenType type) {
        this.row = row;
        this.customRow = row;
        this.column = column;
        this.value = value;
        this.type = type;
    }

    protected Token(int row, int customRow, int column, String value, TokenType type) {
        this.row = row;
        this.customRow = customRow;
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public String value() {
        return value;
    }

    public int row() {
        return customRow;
    }

    public int column() {
        return column;
    }

    public TokenType tokenType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        return row == token.row && customRow == token.customRow && column == token.column && value.equals(token.value) && type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, customRow, column, value, type);
    }

    @Override
    public String toString() {
        var shape = "<%s|r=%d|dr=%d|c=%d|v='%s'>";
        return shape.formatted(type, row, customRow, column, value);
    }

}
