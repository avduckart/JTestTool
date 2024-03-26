package Model.ScriptScanner.Substitutions;

import Model.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InvertSubstitution extends Substitution{
    private final String regExp = "invert\\([\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static InvertSubstitution instance = new InvertSubstitution();

    private InvertSubstitution(){
    }

    public static InvertSubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        byte[] bytes = Utilities.stringToBytes(text);
        int length = bytes.length;
        byte tmp;
        for (int i = 0; i < length/2; i++){
            tmp = bytes[i];
            bytes[i] = bytes[length - 1 - i];
            bytes[length - 1 - i] = tmp;
        }
        return Utilities.bytesToString(bytes);
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
