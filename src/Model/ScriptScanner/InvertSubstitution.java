package Model.ScriptScanner;

import Model.XToY;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InvertSubstitution extends Substitution{
    private final String regExp = "invert\\([\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Substitution instance = new InvertSubstitution();

    private InvertSubstitution(){
    }

    @Override
    protected String execute(String text) {
        matcher.reset();
        byte[] bytes = XToY.stringToBytes(text);
        int length = bytes.length;
        byte tmp;
        for (int i = 0; i < length/2; i++){
            tmp = bytes[i];
            bytes[i] = bytes[length - 1 - i];
            bytes[length - 1 - i] = tmp;
        }
        return XToY.bytesToString(bytes);
    }
}
