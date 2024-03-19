package Model.ScriptScanner;

import java.util.HashMap;
import java.util.regex.Matcher;

abstract public class Substitution {
    private String line;
    private String regExp;
    private Matcher matcher;
    private static Substitution instance;

    protected static final HashMap<String, String> variablesMap = new HashMap<>();

    public static Substitution getInstance(String line) {
        instance.reset(line);
        return instance;
    }

    protected String extractBtwBrackets(String s){
        int firstInd = s.indexOf('(');
        int secondInd = firstInd + s.substring(firstInd).indexOf(')');
        return  s.substring(firstInd + 1, secondInd);
    }

    abstract protected String execute(String str);

    public void reset(String line){
        this.line = line;
        matcher.reset(line);
    }

    public boolean isFound(){
        return matcher.find();
    }

    public String replace(String line){
        matcher.reset(line);
        line = line.replaceAll(regExp, execute(matcher.group()));

        return line;
    }
}
