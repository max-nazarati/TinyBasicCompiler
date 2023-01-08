package tbc.lexer;

public record Lexeme(String stringValue) {

    public Lexeme(Character character) {
        this(character.toString());
    }

}
