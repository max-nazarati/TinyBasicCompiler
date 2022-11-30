package tbc.lexer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
                    .hasMessage("line <1> is could not be parsed");
        }

        @Test
        void throwWhenInvalidLineNumber() {
            // GIVEN
            var line = new Line(1, "100, a");

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseLine(line)).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("line <1> is could not be parsed");
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
        void throwsIfKeywordAfterPrint() {
            // GIVEN
            var line = new Token(1, 0, "PRINT IF a", TokenType.LINE);

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("unexpected keyword found at <1:5>");
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
            // TODO wrong column calculation in error message
            var line = new Token(1, 0, "IF a THEN THEN", TokenType.LINE);

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("unexpected keyword found at <1:10>");
        }

        @ParameterizedTest
        @ValueSource(strings = {"RETURN", "CLEAR", "LIST", "RUN", "END"})
        void parameterLessKeywords(String keyword) {
            // GIVEN
            var line = new Token(1, 0, keyword, TokenType.LINE);

            // WHEN
            List<Token> result = Tokeniser.tokeniseKeywords(line);

            // THEN
            assertThat(result).hasSize(1).containsExactly(new Token(1, 0, keyword, TokenType.KEYWORD));
        }

        @Test
        void parameterlessKeywordThrowsWhenParameters() {
            // GIVEN
            var line = new Token(1, 0, "RETURN 1", TokenType.LINE);

            // WHEN THEN
            assertThatThrownBy(() -> Tokeniser.tokeniseKeywords(line)).isInstanceOf(RuntimeException.class)
                    .hasMessage("text was found after a parameterless keyword at <1:0>");
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