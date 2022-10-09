package tbc.lexer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokeniserTest {

    @Nested
    class LineTokenisation {

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
                    .hasMessage("LINE PARSING FAILED");
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

    @Nested
    class KeywordTokenisation {

        @Test
        void tokeniseKeywords() {
            // GIVEN
            var line = new LineToken(1, 0, "100 PRINT a IF aa THEN aaa GOTO a INPUT a LET A GOSUB A RETURN a CLEAR LIST RUN END");
            var expectedTokens = List.of(
                    new KeywordToken(1, 5, Keyword.valueOf("PRINT")),
                    new KeywordToken(1, 13, Keyword.valueOf("IF")),
                    new KeywordToken(1, 19, Keyword.valueOf("THEN")),
                    new KeywordToken(1, 28, Keyword.valueOf("GOTO")),
                    new KeywordToken(1, 35, Keyword.valueOf("INPUT")),
                    new KeywordToken(1, 43, Keyword.valueOf("LET")),
                    new KeywordToken(1, 49, Keyword.valueOf("GOTO")),
                    new KeywordToken(1, 57, Keyword.valueOf("RETURN")),
                    new KeywordToken(1, 66, Keyword.valueOf("CLEAR")),
                    new KeywordToken(1, 74, Keyword.valueOf("LIST")),
                    new KeywordToken(1, 77, Keyword.valueOf("RUN")),
                    new KeywordToken(1, 81, Keyword.valueOf("END"))
            );

            // WHEN
            List<KeywordToken> result = Tokeniser.tokeniseKeywords(line);

            // THEN
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void throwsForInvalidKeyword() {
            // GIVEN
            var line = new LineToken(1, 0, "100 PRI a");

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line)).isInstanceOf(RuntimeException.class).hasMessage(String.format(
                    "FAILED TO PARSE KEYWORD AT: [%d, %d]", 1, 5));
        }

    }

}