package tbc.lexer.exception;

public enum ParsingException {
    TEXT_AFTER_PARAMETERLESS_KEYWORD("text was found after a parameterless keyword at <%d:%d>"),
    UNEXPECTED_KEYWORD_FOUND("unexpected keyword found at <%d:%d>"),
    LINE_NOT_PARSABLE("line <%d> is could not be parsed");

    private final String errorMessage;

    public String errorMessage() {
        return errorMessage;
    }

    ParsingException(String errorMessage) {

        this.errorMessage = errorMessage;
    }
}
