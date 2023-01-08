package tbc.lexer;

public record Token(int id, int row, int column, TokenType type, Lexeme lexeme) {

}
