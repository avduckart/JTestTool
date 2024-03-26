package Model.ScriptScanner.Substitutions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VariableSubstitution extends Substitution {
    private final String regExp = "%\\w+%";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static VariableSubstitution instance = new VariableSubstitution();

    private VariableSubstitution(){
    }

    public static VariableSubstitution getInstance(String line){
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
        ArrayList<String> varList = extractVariables(line);
        for (String x : varList)
            line = line.replace(x, variablesMap.get(x));
        return line;
    }

    private ArrayList<String> extractVariables(String line) {
        Matcher varDeclMatcher = Pattern.compile("%\\w+%").matcher(line);
        ArrayList<String> varList = new ArrayList<>();
        while(varDeclMatcher.find())
            varList.add(varDeclMatcher.group());
        return varList;
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
