package tbc.lexer.pipe;

public enum PipeState {
    END(null),
    NO_BLOBS(END),
    WITH_TOP_LVL_KEYWORDS(NO_BLOBS),
    WITH_LINES(WITH_TOP_LVL_KEYWORDS),
    INIT(WITH_LINES);

    private final PipeState next;

    public PipeState next() {
        return next;
    }

    PipeState(PipeState next) {
        this.next = next;
    }
}
