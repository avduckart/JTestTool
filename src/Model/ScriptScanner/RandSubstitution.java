package Model.ScriptScanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RandSubstitution extends Substitution{
    private final String regExp = "rand\\(\\d+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static RandSubstitution instance = new RandSubstitution();

    private RandSubstitution(){
    }

    public static RandSubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        int byteLength = Integer.parseInt(text);
        return String.format("%02X", (int)(Math.random()*256*byteLength));
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
