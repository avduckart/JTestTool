package Model.ScriptScanner.Substitutions;

import Model.APDUTestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Model.Calculation.Calculator.operator;

public final class MathOperationSubstitution extends Substitution{
    private final String regExp = "\\([\\dA-F]+[+*\\-/%][\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");

    public MathOperationSubstitution(){
    }

    @Override
    protected String execute(String line) {
        try {
            String findExpr = matcher.group();
            char sign = 0;
            sign = extractSign(findExpr);
            String arg1 = findExpr.split("[+\\-/*%]")[0].replaceFirst("\\(","");
            String arg2 = findExpr.split("[+\\-/*%]")[1].replaceFirst("\\)$","");
            String result = calculate(arg1, arg2, sign);
            line = line.replace(findExpr, result);
        } catch (APDUTestException e) {
            throw new RuntimeException(e);
        }

        return line;
    }

    private char extractSign(String s) throws APDUTestException {
        String sign = "[+\\-/*%]";
        Pattern pattern = Pattern.compile(sign);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find())
            return matcher.group().charAt(0);
        else
            throw new APDUTestException();
    }

    private String calculate(String arg1, String arg2, char op) throws APDUTestException {
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
