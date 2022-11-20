package tbc.lexer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokeniserTest {

    @Nested
    class LineTokenisation {

        @ParameterizedTest
        @MethodSource("argsProvider")
        void tokeniseLine(int customRow, String lineString) {
            // GIVEN
            var line = new Line(2, lineString);
            var expectedToken = new Token(2, customRow, 0, lineString, TokenType.LINE);

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

        private static Stream<Arguments> argsProvider() {
            return Stream.of(
                    Arguments.of(100, "100 a"),
                    Arguments.of(200, "200 a b"),
                    Arguments.of(300, "300 alksdj lkajsd; _ ljfd"),
                    Arguments.of(2, "some words without contents number")
            );

        }

    }

    @Nested
    class KeywordTokenisation {

        @Test
        void tokenisePrint() {
            // GIVEN
            var line = new Token(1, 0, "PRINT alasdj adf 23420 lkjjsf", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "PRINT", TokenType.KEYWORD),
                    new Token(1, 5, " alasdj adf 23420 lkjjsf", TokenType.BLOB)
            );

            // WHEN
            List<Token> result = Tokeniser.tokeniseKeywords(line);

            // THEN
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void tokeniseIf() {
            // GIVEN
            var line = new Token(1, 0, "IF alasdj adf THEN 23420 lkjjsf", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "IF", TokenType.KEYWORD),
                    new Token(1, 2, " alasdj adf ", TokenType.BLOB),
                    new Token(1, 14, "THEN", TokenType.KEYWORD),
                    new Token(1, 18, " 23420 lkjjsf", TokenType.BLOB)
            );

            // WHEN
            List<Token> result = Tokeniser.tokeniseKeywords(line);

            // THEN
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void throwsIfInvalidIfLine() {
            // GIVEN
            var line = new Token(1, 0, "IF a THEN THEN", TokenType.LINE);

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("KEYWORD PARSING FAILED 1");
        }

        @Test
        void throwsIfKeywordAfterPrint() {
            // GIVEN
            var line = new Token(1, 0, "PRINT IF a", TokenType.LINE);

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("KEYWORD PARSING FAILED 1");
        }

        @Test
        void throwsIfInvalidKeyword() {
            // GIVEN
            var line = new Token(1, 0, "PRI IF a", TokenType.LINE);

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

}