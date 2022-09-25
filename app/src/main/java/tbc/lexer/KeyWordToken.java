package tbc.lexer;

class KeyWordToken extends Token<String> {

    protected KeyWordToken(int row, int column, String value) {
        super(row, column, value, TokenType.KEYWORD);
    }

}
