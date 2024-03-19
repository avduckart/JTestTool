package Model.ScriptScanner;

import Model.Crypto.ECPointOperation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiplePointSubstitution extends Substitution{
    private final String regExp = "mulpoint\\([\\dA-F]{128},[\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Substitution instance = new MultiplePointSubstitution();

    private MultiplePointSubstitution(){
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        String[] pointAndMult = extractBtwBrackets(text).split(",+");
        String point = pointAndMult[0];
        String multiplier = pointAndMult[1];
        return ECPointOperation.getMultipledPoint(point, multiplier);
    }
}
