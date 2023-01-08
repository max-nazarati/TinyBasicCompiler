package tbc.lexer;

record Lexeme(String stringValue) {
    public Lexeme(Character character) {
        this(character.toString());
    }

}
