package Substitutions;

import JTestCrypto.EncryptionGOST_TC26;
import utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EcbSubstitution extends Substitution{
    private final String regExp = "[ed]{1}ecb\\([\\dA-F]+,[\\dA-F]{64}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");

    @Override
    protected String execute(String message) {
        matcher.reset();
        boolean toEncrypt = message.charAt(0) == 'e';
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        byte[] text = Utils.stringToBytes(textAndKey[0]);
        byte[] key = Utils.stringToBytes(textAndKey[1]);
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();

        return Utils.bytesToString(cipher.ecb(text, key, toEncrypt));
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
