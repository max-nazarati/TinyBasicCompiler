package tbc.lexer;

class KeywordToken extends Token<String> {

    protected KeywordToken(int row, int column, String value) {
        super(row, column, value, TokenType.KEYWORD);
    }

}
