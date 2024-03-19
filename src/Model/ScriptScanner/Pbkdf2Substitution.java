package Model.ScriptScanner;

import Model.Crypto.HMAC_3411;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pbkdf2Substitution extends Substitution{
    private final String regExp = "pbkdf2\\([\\dA-F]+,[\\dA-F]+,\\d+,\\d+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Substitution instance = new Pbkdf2Substitution();

    private Pbkdf2Substitution(){
    }

    @Override
    protected String execute(String line) {
        matcher.reset();
        String[] pbkdfArgs = extractBtwBrackets(line).split(",+");
        String password = pbkdfArgs[0];
        String salt = pbkdfArgs[1];
        String count = pbkdfArgs[2];
        String dkLen = pbkdfArgs[3];
        return HMAC_3411.pbkdf2(password, salt, count, dkLen);
    }
}
