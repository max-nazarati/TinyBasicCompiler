package tbc.lexer;

class KeywordToken extends Token<Keyword> {

    protected KeywordToken(int row, int column, Keyword value) {
        super(row, column, value, TokenType.KEYWORD);
    }

}
