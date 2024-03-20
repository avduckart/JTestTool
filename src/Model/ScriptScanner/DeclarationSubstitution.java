package Model.ScriptScanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DeclarationSubstitution extends Substitution{
    private final String regExp = "^(%\\w+%)=";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private static final DeclarationSubstitution instance = new DeclarationSubstitution();

    private DeclarationSubstitution(){
    }

    public static Substitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String str) {
        return "";
    }

    @Override
    public String replace(String line) {
        int begin = line.indexOf('%');
        int end = line.indexOf('=');

        if(begin == -1 || end == -1)
            return line;

        String key = line.substring(begin, end);
        String value = line.substring(line.indexOf('=') + 1);

        ValueSubstitution valueSubst = (ValueSubstitution) ValueSubstitution.getInstance(line);

        if(valueSubst.getMatcher().find())
            value = valueSubst.replace(value);

        variablesMap.put(key, value);

        return line;
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