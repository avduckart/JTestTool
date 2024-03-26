package Model.ScriptScanner.Substitutions;

import Model.JTestCrypto.ECPointOperation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SumPointSubstitution extends Substitution{
    private final String regExp = "addpoint\\([\\dA-F]{128},[\\dA-F]{128}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static SumPointSubstitution instance = new SumPointSubstitution();

    private SumPointSubstitution(){
    }

    public static SumPointSubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        String[] pointAndPoint = extractBtwBrackets(text).split(",+");
        String point1 = pointAndPoint[0];
        String point2 = pointAndPoint[1];
        return ECPointOperation.sumPoint(point1, point2);
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
