package Model.ScriptScanner;

import Model.JTestCrypto.EncryptionGOST_TC26;
import Model.Utilities;
import org.bouncycastle.crypto.CryptoException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CfbSubstitution extends Substitution{
    private final String regExp = "cfb\\([\\dA-F]+,[\\dA-F]{16},[\\dA-F]{64}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static CfbSubstitution instance = new CfbSubstitution();

    private CfbSubstitution() {
    }

    public static CfbSubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    public String execute(String message) {
        matcher.reset();
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        byte[] text = Utilities.stringToBytes(textAndKey[0]);
        byte[] iv = Utilities.stringToBytes(textAndKey[1]);
        byte[] key = Utilities.stringToBytes(textAndKey[2]);
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        try {
            return Utilities.bytesToString(cipher.cfb(text, iv, key));
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
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
