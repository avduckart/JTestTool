package Substitutions;

import java.util.HashMap;
import java.util.regex.Matcher;

abstract public class Substitution {

    protected static final HashMap<String, String> variablesMap = new HashMap<>();

    abstract protected String execute(String str);
    abstract protected String getRegExp();
    abstract protected Matcher getMatcher();

    public void reset(String line){
        getMatcher().reset(line);
    }

    protected String extractBtwBrackets(String line){
        int firstInd = line.indexOf('(');
        int secondInd = firstInd + line.substring(firstInd).indexOf(')');
        return line.substring(firstInd + 1, secondInd);
    }

    public String replace(String line){
        reset(line);

        if(getMatcher().find(0))
            line = line.replaceAll(getRegExp(), execute(getMatcher().group()));

        return line;
    }

    public boolean isFound(){
        return getMatcher().find();
    }
}
