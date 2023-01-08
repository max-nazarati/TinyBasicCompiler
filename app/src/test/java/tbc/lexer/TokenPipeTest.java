package tbc.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

class TokenPipeTest {

    @Test
    void tokenise() {
        var x = new TokenPipe(List.of(List.of('P', 'R', 'I', 'N', 'T', ' ', 'a', 'h', 'a', ' ', '4', '2', ' ', '!')), List.of());
        var y = x.tokenise();
        var z = 1;
    }

}