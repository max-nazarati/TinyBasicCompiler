package tbc.lexer;

class LineToken extends Token<String> {

    protected LineToken(int row, int column, String value) {
        super(row, column, value, TokenType.STRING);
    }

}
