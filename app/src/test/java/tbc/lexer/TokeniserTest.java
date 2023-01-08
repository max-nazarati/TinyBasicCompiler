package tbc.lexer;

import org.junit.jupiter.api.Test;
import tbc.enums.TokenType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokeniserTest {

    @Test
    void tokenise() {
        var tokeniser = new Tokeniser(List.of(List.of(
                'P',
                'R',
                'I',
                'N',
                'T',
                ' ',
                '4',
                '2',
                ' ',
                '"',
                '!',
                '!',
                '!',
                '"',
                'Z'
        )), List.of());
        var result = tokeniser.tokenise();
        var expected = List.of(
                new Token(0, 0, 0, TokenType.KEYWORD, new Lexeme("PRINT")),
                new Token(1, 0, 5, TokenType.WHITESPACE, new Lexeme(" ")),
                new Token(2, 0, 6, TokenType.NUMBER, new Lexeme("42")),
                new Token(3, 0, 8, TokenType.WHITESPACE, new Lexeme(" ")),
                new Token(4, 0, 9, TokenType.STRING, new Lexeme("!!!")),
                new Token(5, 0, 14, TokenType.VARIABLE, new Lexeme("Z"))
        );

        assertThat(result.tokens()).containsExactlyElementsOf(expected);
    }

}