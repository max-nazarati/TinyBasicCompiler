package tbc.lexer;

import java.util.Objects;

public abstract class Token<T> {
    private int row;
    private int column;
    private final T value;
    private final TokenType type;

    protected Token(int row, int column, T value, TokenType type) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public T value() {
        return value;
    }

    public TokenType type() {
        return type;
    }

    public int row() {
        return row;
    }

    public void incrementRow() {
        row++;
    }

    public void decrementRow() {
        row--;
    }

    public int column() {
        return column;
    }

    public void incrementColumn() {
        column++;
    }

    public void decrementColumn() {
        column--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token<?> token = (Token<?>) o;
        return row == token.row && column == token.column && value.equals(token.value) && type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, value, type);
    }

}
