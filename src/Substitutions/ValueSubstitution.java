package Substitutions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValueSubstitution extends Substitution{
    private final String regExp = "value\\(\\d+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");

    @Override
    protected String execute(String line){
//        matcher.reset();
//        extractValueFromResponse(Integer.parseInt(extractBtwBrackets(value)));
//        receivedResponse
//        String value;
//        while(matcher.find()) {
//            value = matcher.group();
//            line = line.replaceFirst("value\\(\\d+\\)", extractValueFromResponse(Integer.parseInt(extractBtwBrackets(value))));
//            matcher.reset();
//        }

        return "line";
    }

//    private String extractValueFromResponse(int num){
//        String length;
//        int pos = 0;
//        int addLen = 0;
//        for(int i = 1; i < num; i++){
//            length = extractBtwBrackets(expectedResponse.substring(pos));
//            addLen += 2*Integer.parseInt(length);
//            pos += expectedResponse.indexOf('L') + 1;
//        }
//        String substr = expectedResponse.substring(pos);
//        int from = pos + substr.indexOf('(') + addLen - 4*(num-1);
//        length = extractBtwBrackets(substr);
//        return receivedResponse.substring(from, from + 2*Integer.parseInt(length));
//    }



    @Override
    protected String getRegExp() {
        return regExp;
    }

    @Override
    protected Matcher getMatcher() {
        return matcher;
    }
}
