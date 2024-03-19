package Model.ScriptScanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DeclarationSubstitution extends Substitution{
    private final String regExp = "^(%\\w+%)=";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private static final Substitution instance = new DeclarationSubstitution();

    private DeclarationSubstitution(){
    }

    @Override
    protected String execute(String str) {
        return "";
    }

    @Override
    public String replace(String line) {
        String key = line.substring(line.indexOf('%'), line.indexOf('='));
        String value = line.substring(line.indexOf('=') + 1);

        ValueSubstitution valueSubst = (ValueSubstitution) ValueSubstitution.getInstance(line);

        if(valueSubst.getMatcher().find())
            value = valueSubst.replace(value);

        variablesMap.put(key, value);

        return line;
    }
}