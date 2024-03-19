package Model.ScriptScanner;

import Model.JTestCrypto.JTestHMac.JTestHMac;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HmacSubstitution extends Substitution{
    private final String regExp = "hmac(094|256|512)\\([\\dA-F]+,[\\dA-F]{64}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Substitution instance = new HmacSubstitution();

    private HmacSubstitution(){
    }

    @Override
    protected String execute(String line){
        matcher.reset();
        String hmacAlg = line.substring(4,7);
        String[] hmacArgs = extractBtwBrackets(line).split(",+");
        String text = hmacArgs[0];
        String key = hmacArgs[1];
        JTestHMac hmac = new JTestHMac(hmacAlg);
        return hmac.execute(text, key);
    }
}
