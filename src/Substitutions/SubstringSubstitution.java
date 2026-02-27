package Substitutions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubstringSubstitution extends Substitution{
    private static final String regExp = "substr\\([\\dA-F]+,\\d+,\\d+\\)";
    private static final Matcher matcher = Pattern.compile(regExp).matcher("");

    @Override
    protected String execute(String str) {
        return "";
    }

    @Override
    public String replace(String line) {
        matcher.reset(line);
        String[] text;
        int start, end;
        while (matcher.find()){
            text = extractBtwBrackets(matcher.group()).split(",+");
            start = Integer.parseInt(text[1]);
            end = Integer.parseInt(text[2]);
            line = line.replaceAll(regExp, getSubstr(text[0], start, end));
            matcher.reset(line);
        }
        return line;
    }

    private String getSubstr(String text, int start, int end) {
        matcher.reset();
        return text.substring(2*start, 2*end);
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
