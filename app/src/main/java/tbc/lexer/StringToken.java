package tbc.lexer;

class StringToken extends Token<String> {

    protected StringToken(int row, int column, String value) {
        super(row, column, value, TokenType.STRING);
    }

}
