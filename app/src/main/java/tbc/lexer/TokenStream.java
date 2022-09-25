package tbc.lexer;

import java.util.List;

public class TokenStream {
    private List<Token> tokens;
    private int currentIndex = 0;

    public TokenStream(List<Character> characters) {
        this.tokens = tokenise(characters);
    }

    private List<Token> tokenise(List<Character> characters) {
        return null;
    }

}
