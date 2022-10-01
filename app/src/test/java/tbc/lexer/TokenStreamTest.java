package tbc.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenStreamTest {

    @Test
    void tokeniseLine() {
        // GIVEN
        var lines = Stream.of("100 a", "200 a b", "300 alksdj lkajsd; _ ljfd", "some words without line number");
        var tokenisedLines = List.of(
                new LineToken(0, 0, "100 a"),
                new LineToken(0, 0, "200 a b"),
                new LineToken(0, 0, "300 alksdj lkajsd; _ ljfd"),
                new LineToken(0, 0, "some words without line number")
        );

        // WHEN
        var result = new TokenStream(lines).getTokens();

        // THEN
        assertThat(result).isEqualTo(tokenisedLines);
    }

    @Test
    void throwWhenEndsWithWhitespace() {
        // GIVEN
        var lines = Stream.of("100 a ");

        // WHEN THEN
        assertThatThrownBy(() -> new TokenStream(lines).getTokens()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("LINE PARSING FAILED");
    }

    @Test
    void throwWhenInvalidLineNumber() {
        // GIVEN
        var lines = Stream.of("100, a");

        // WHEN THEN
        assertThatThrownBy(() -> new TokenStream(lines).getTokens()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("LINE PARSING FAILED");
    }

}