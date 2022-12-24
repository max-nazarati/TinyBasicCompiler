package tbc.lexer;

public record Token(int row, int customRow, int column, String value, TokenType type) {
    // TODO: turn into a record?

    public Token(int row, int column, String value, TokenType type) {
        this(row, row, column, value, type);
    }

    @Override
    public String toString() {
        var shape = "<%s|r=%d|dr=%d|c=%d|v='%s'>";
        return shape.formatted(type, row, customRow, column, value);
    }

}
