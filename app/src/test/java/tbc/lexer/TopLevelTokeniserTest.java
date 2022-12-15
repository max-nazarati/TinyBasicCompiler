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

class TopLevelTokeniserTest {

    @Nested
    class LineTokenisation {

        @ParameterizedTest
        @MethodSource("argsProvider")
        void tokeniseLine(int customRow, String lineString) {
            // given
            var line = new Line(2, lineString);
            var expectedToken = new Token(2, customRow, 0, lineString, TokenType.LINE);

            // when
            var result = TopLevelTokeniser.tokeniseLine(line);

            // then
            assertThat(result).isEqualTo(expectedToken);
        }

        @Test
        void throwWhenEndsWithWhitespace() {
            // given
            Line line = new Line(1, "100 a ");

            // when THEN
            assertThatThrownBy(() -> TopLevelTokeniser.tokeniseLine(line)).isInstanceOf(RuntimeException.class)
                    .hasMessage("line <1> is could not be parsed");
        }

        @Test
        void throwWhenInvalidLineNumber() {
            // given
            var line = new Line(1, "100, a");

            // when THEN
            assertThatThrownBy(() -> TopLevelTokeniser.tokeniseLine(line)).isInstanceOf(RuntimeException.class)
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
            // given
            var line = new Token(1, 0, "PRINT alasdj adf 23420 lkjjsf", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "PRINT", TokenType.KEYWORD),
                    new Token(1, 5, " alasdj adf 23420 lkjjsf", TokenType.BLOB)
            );

            // when
            List<Token> result = TopLevelTokeniser.tokeniseKeywords(line);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void throwsIfKeywordAfterPrint() {
            // given
            var line = new Token(1, 0, "PRINT IF a", TokenType.LINE);

            // when THEN
            assertThatThrownBy(() -> TopLevelTokeniser.tokeniseKeywords(line))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("unexpected keyword found at <1:5>");
        }

        @Test
        void tokeniseIf() {
            // given
            var line = new Token(1, 0, "IF alasdj adf THEN 23420 lkjjsf", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "IF", TokenType.KEYWORD),
                    new Token(1, 2, " alasdj adf ", TokenType.BLOB),
                    new Token(1, 14, "THEN", TokenType.KEYWORD),
                    new Token(1, 18, " 23420 lkjjsf", TokenType.BLOB)
            );

            // when
            List<Token> result = TopLevelTokeniser.tokeniseKeywords(line);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void throwsIfInvalidIfLine() {
            // given
            // TODO wrong column calculation in error message
            var line = new Token(1, 0, "IF a THEN THEN", TokenType.LINE);

            // when THEN
            assertThatThrownBy(() -> TopLevelTokeniser.tokeniseKeywords(line))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("unexpected keyword found at <1:10>");
        }

        @ParameterizedTest
        @ValueSource(strings = {"RETURN", "CLEAR", "LIST", "RUN", "END"})
        void parameterLessKeywords(String keyword) {
            // given
            var line = new Token(1, 0, keyword, TokenType.LINE);

            // when
            List<Token> result = TopLevelTokeniser.tokeniseKeywords(line);

            // then
            assertThat(result).hasSize(1).containsExactly(new Token(1, 0, keyword, TokenType.KEYWORD));
        }

        @Test
        void parameterlessKeywordThrowsWhenParameters() {
            // given
            var line = new Token(1, 0, "RETURN 1", TokenType.LINE);

            // when THEN
            assertThatThrownBy(() -> TopLevelTokeniser.tokeniseKeywords(line)).isInstanceOf(RuntimeException.class)
                    .hasMessage("text was found after a parameterless keyword at <1:0>");
        }

        @Test
        void throwsIfInvalidKeyword() {
            // given
            var line = new Token(1, 0, "PRI IF a", TokenType.LINE);

            // when THEN
            assertThatThrownBy(() -> TopLevelTokeniser.tokeniseKeywords(line))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    class BlobResolution {

        @Test
        void ifElseBlobs() {
            // given
            var line = new Token(1, 0, "IF alasdj < adf THEN 23420 lkjjsf", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "IF", TokenType.KEYWORD),
                    new Token(1, 10, "<", TokenType.RELOP),
                    new Token(1, 3, "alasdj", TokenType.EXPRESSION),
                    new Token(1, 12, "adf", TokenType.EXPRESSION),
                    new Token(1, 16, "THEN", TokenType.KEYWORD),
                    new Token(1, 21, "23420 lkjjsf", TokenType.STATEMENT)
            );

            // when
            List<Token> tokensWithBlobs = TopLevelTokeniser.tokeniseKeywords(line);
            List<Token> result = TopLevelTokeniser.resolveBlobs(tokensWithBlobs);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void gotoBlob() {
            // given
            var line = new Token(1, 0, "GOTO some commands", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "GOTO", TokenType.KEYWORD),
                    new Token(1, 5, "some commands", TokenType.EXPRESSION)
            );

            // when
            List<Token> tokensWithBlobs = TopLevelTokeniser.tokeniseKeywords(line);
            List<Token> result = TopLevelTokeniser.resolveBlobs(tokensWithBlobs);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void inputBlob() {
            // given
            var line = new Token(1, 0, "INPUT some commands", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "INPUT", TokenType.KEYWORD),
                    new Token(1, 6, "some commands", TokenType.VAR_LST)
            );

            // when
            List<Token> tokensWithBlobs = TopLevelTokeniser.tokeniseKeywords(line);
            List<Token> result = TopLevelTokeniser.resolveBlobs(tokensWithBlobs);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void printBlob() {
            // given
            var line = new Token(1, 0, "PRINT some commands", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "PRINT", TokenType.KEYWORD),
                    new Token(1, 6, "some commands", TokenType.EXPR_LST)
            );

            // when
            List<Token> tokensWithBlobs = TopLevelTokeniser.tokeniseKeywords(line);
            List<Token> result = TopLevelTokeniser.resolveBlobs(tokensWithBlobs);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void letBlob() {
            // given
            var line = new Token(1, 0, "LET a = 10", TokenType.LINE);
            var expectedTokens = List.of(
                    new Token(1, 0, "LET", TokenType.KEYWORD),
                    new Token(1, 6, "=", TokenType.ASSIGNMENT),
                    new Token(1, 4, "a", TokenType.VAR),
                    new Token(1, 8, "10", TokenType.EXPRESSION)
            );

            // when
            List<Token> tokensWithBlobs = TopLevelTokeniser.tokeniseKeywords(line);
            List<Token> result = TopLevelTokeniser.resolveBlobs(tokensWithBlobs);

            // then
            assertThat(result).isEqualTo(expectedTokens);
        }

        @Test
        void throwsWhenInvalidKeyword() {
            var tokens = List.of(
                    new Token(1, 0, "LETT", TokenType.KEYWORD),
                    new Token(1, 0, " a = b", TokenType.BLOB)
            );

            // when then
            assertThatThrownBy(() -> TopLevelTokeniser.resolveBlobs(tokens)).isInstanceOf(RuntimeException.class)
                    .hasMessage("sth went wrong while parsing blobs");
        }

        @Test
        void throwsWhenInvalidIfBody() {
            var tokens = List.of(
                    new Token(1, 0, "IF", TokenType.KEYWORD),
                    new Token(1, 0, " a <= b", TokenType.BLOB)
            );

            // when then
            assertThatThrownBy(() -> TopLevelTokeniser.resolveBlobs(tokens)).isInstanceOf(RuntimeException.class)
                    .hasMessage("could not parse IF body");
        }

        @Test
        void throwsWhenInvalidLetAssignment() {
            var tokens = List.of(
                    new Token(1, 0, "LET", TokenType.KEYWORD),
                    new Token(1, 0, " a == b", TokenType.BLOB)
            );

            // when then
            assertThatThrownBy(() -> TopLevelTokeniser.resolveBlobs(tokens)).isInstanceOf(RuntimeException.class)
                    .hasMessage("this does not look like a correct assignment");
        }

    }

}