package Substitutions;

import JTestCrypto.EncryptionGOST_TC26;
import utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CfbSubstitution extends Substitution{
    private final String regExp = "cfb\\([\\dA-F]+,[\\dA-F]{16},[\\dA-F]{64}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");

    @Override
    public String execute(String message) {
        matcher.reset();
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        byte[] text = Utils.stringToBytes(textAndKey[0]);
        byte[] iv = Utils.stringToBytes(textAndKey[1]);
        byte[] key = Utils.stringToBytes(textAndKey[2]);
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();

        return Utils.bytesToString(cipher.cfb(text, iv, key));
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
