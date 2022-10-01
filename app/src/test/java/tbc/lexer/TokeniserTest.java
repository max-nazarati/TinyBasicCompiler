package tbc.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokeniserTest {

    @ParameterizedTest
    @ValueSource(strings = {"100 a", "200 a b", "300 alksdj lkajsd; _ ljfd", "some words without contents number"})
    void tokeniseLine(String lineString) {
        // GIVEN
        var line = new Line(2, lineString);
        var expectedToken = new LineToken(2, 0, lineString);

        // WHEN
        var result = Tokeniser.tokeniseLine(line);

        // THEN
        assertThat(result).isEqualTo(expectedToken);
    }

    @Test
    void throwWhenEndsWithWhitespace() {
        // GIVEN
        Line line = new Line(1, "100 a ");

        // WHEN THEN
        assertThatThrownBy(() -> Tokeniser.tokeniseLine(line)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("LINE PARSING FAILED");
    }

    @Test
    void throwWhenInvalidLineNumber() {
        // GIVEN
        var line = new Line(1, "100, a");

        // WHEN THEN
        assertThatThrownBy(() -> Tokeniser.tokeniseLine(line)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("LINE PARSING FAILED");
    }

}