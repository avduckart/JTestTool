package Model.ScriptScanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValueSubstitution extends Substitution{
    private final String regExp = "value\\(\\d+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Substitution instance = new ValueSubstitution();

    private ValueSubstitution(){
    }

    @Override
    protected String execute(int num){
        String length;
        int pos = 0;
        int addLen = 0;
        for(int i = 1; i < num; i++){
            length = extractBtwBrackets(expectedResponse.substring(pos));
            addLen += 2*Integer.parseInt(length);
            pos += expectedResponse.indexOf('L') + 1;
        }
        String substr = expectedResponse.substring(pos);
        int from = pos + substr.indexOf('(') + addLen - 4*(num-1);
        length = extractBtwBrackets(substr);
        return receivedResponse.substring(from, from + 2*Integer.parseInt(length));
    }

    @Override
    protected String execute(String str) {
        return "";
    }

    public Matcher getMatcher() {
        return matcher;
    }
}
