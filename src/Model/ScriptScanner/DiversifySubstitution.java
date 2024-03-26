package Model.ScriptScanner;

import Model.JTestCrypto.ECPointOperation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiversifySubstitution extends Substitution{
    private final String regExp = "diverse\\([\\dA-F]+,[\\dA-F]{64},[\\dA-F]{128}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static DiversifySubstitution instance = new DiversifySubstitution();

    private DiversifySubstitution(){
    }

    public static DiversifySubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        String[] args = extractBtwBrackets(text).split(",+");
        String ukm = args[0];
        String ck = args[1];
        String ok = args[2];
        return ECPointOperation.diverseKey(ukm, ck, ok);
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
