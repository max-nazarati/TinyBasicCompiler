package tbc.lexer;

import java.util.Objects;

public class Token {

    private final int row;
    private final int column;
    private final String value;
    private final TokenType type;

    protected Token(int row, int column, String value, TokenType type) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public String value() {
        return value;
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    @Override
    public String toString() {
        return "Token{" +
                "row=" + row +
                ", column=" + column +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
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
        return row == token.row && column == token.column && value.equals(token.value) && type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, value, type);
    }

}
