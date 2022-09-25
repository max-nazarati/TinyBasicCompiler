package tbc.lexer;

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

}
