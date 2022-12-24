package tbc.lexer;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExpressionTokeniser {

    private ExpressionTokeniser() {

    }

    public List<Token> tokeniseExpression(Token expression) {
        if (!expression.type().equals(TokenType.EXPRESSION)) {
            throw new RuntimeException("tried parsing expression but token was not an expression instance");
        }

//        Function<Character, Character> f = c -> c == '-' || c == '+' ? c : '';
//        int firstSign = f.apply(expression.value().charAt(0), 0);

        Optional<Integer> firstIndexOfMinus = safeSupplierWrapper(() -> expression.value().indexOf('-'), -1);
        Optional<Integer> lastIndexOfMinus = safeSupplierWrapper(() -> expression.value().lastIndexOf('-'), -1);

        Optional<Integer> firstIndexOfPlus = safeSupplierWrapper(() -> expression.value().indexOf('+'), -1);
        Optional<Integer> lastIndexOfPlus = safeSupplierWrapper(() -> expression.value().lastIndexOf('+'), -1);

//        if (Math.abs(firstIndexOfMinus - firstIndexOfPlus) <= 1 || Math.abs(lastIndexOfMinus - lastIndexOfPlus) <= 1) {
//            throw new RuntimeException("error parsing expression, sth is wrong with the +- operators on line [%d:%d]".formatted(
//                    expression.row(),
//                    expression.column()
//            ));
//        }

        Optional<Integer> firstSignIndex = firstIndexOfMinus.map(mIndex -> Math.min(mIndex, firstIndexOfPlus.orElse(mIndex)))
                .or(() -> firstIndexOfPlus);

        Optional<Integer> lastSignIndex = lastIndexOfMinus.map(mIndex -> Math.min(mIndex, lastIndexOfPlus.orElse(mIndex)))
                .or(() -> lastIndexOfPlus);

        return null;

    }

    private static Optional<Integer> optionalIndexOf(String s, int c) {
        int i = s.indexOf(c);
        return Optional.ofNullable(i == -1 ? null : i);
    }

    private static <T> Optional<T> safeSupplierWrapper(Supplier<T> f, T nullEquivalent) {
        T result = f.get();
        return Optional.ofNullable(result == nullEquivalent ? null : result);
    }

}
