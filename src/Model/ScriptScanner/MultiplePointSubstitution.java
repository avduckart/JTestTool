package Model.ScriptScanner;

import Model.JTestCrypto.ECPointOperation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiplePointSubstitution extends Substitution{
    private final String regExp = "mulpoint\\([\\dA-F]{128},[\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static MultiplePointSubstitution instance = new MultiplePointSubstitution();

    private MultiplePointSubstitution(){
    }

    public static MultiplePointSubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        String[] pointAndMult = extractBtwBrackets(text).split(",+");
        String point = pointAndMult[0];
        String multiplier = pointAndMult[1];
        return ECPointOperation.getMultipledPoint(point, multiplier);
    }

    @Override
    protected String getRegExp() {
        return regExp;
    }

    @Override
    protected Matcher getMatcher() {
        return matcher;
    }

    @Override
    protected void reset(String line) {
        matcher.reset(line);
    }
}
