package Substitutions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static Calculation.Calculator.operator;

public final class MathOperationSubstitution extends Substitution {
    private final String regExp = "\\([\\dA-F]+[+*\\-/%][\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");

    @Override
    protected String execute(String line) {
        try {
            String findExpr = matcher.group();
            char sign = extractSign(findExpr);
            String arg1 = findExpr.split("[+\\-/*%]")[0].replaceFirst("\\(","");
            String arg2 = findExpr.split("[+\\-/*%]")[1].replaceFirst("\\)$","");
            String result = calculate(arg1, arg2, sign);
            line = line.replace(findExpr, result);
        } catch (SubstitutionException e) {
            throw new RuntimeException(e);
        }

        return line;
    }

    private char extractSign(String s) throws SubstitutionException {
        Pattern pattern = Pattern.compile("[+\\-/*%]");
        Matcher matcher = pattern.matcher(s);

        if(!matcher.find())
            throw new SubstitutionException();

        return matcher.group().charAt(0);
    }

    private String calculate(String arg1, String arg2, char op) throws SubstitutionException {
        return operator(op, arg1, arg2);
    }

    @Override
    protected String getRegExp() {
        return regExp;
    }

    @Override
    protected Matcher getMatcher() {
        return matcher;
    }
}
