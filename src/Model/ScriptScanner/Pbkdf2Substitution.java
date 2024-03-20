package Model.ScriptScanner;

import Model.JTestCrypto.JTestPbkdf2.JTestPBKDF2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pbkdf2Substitution extends Substitution{
    private final String regExp = "pbkdf2\\([\\dA-F]+,[\\dA-F]+,\\d+,\\d+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Pbkdf2Substitution instance = new Pbkdf2Substitution();

    private Pbkdf2Substitution(){
    }

    public static Pbkdf2Substitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String line) {
        matcher.reset();
        String[] pbkdfArgs = extractBtwBrackets(line).split(",+");
        String password = pbkdfArgs[0];
        String salt = pbkdfArgs[1];
        String count = pbkdfArgs[2];
        String dkLen = pbkdfArgs[3];
        return JTestPBKDF2.execute(password, salt, count, dkLen);
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
