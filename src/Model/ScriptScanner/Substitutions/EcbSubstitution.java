package Model.ScriptScanner.Substitutions;

import Model.JTestCrypto.EncryptionGOST_TC26;
import Model.Utilities;
import org.bouncycastle.crypto.CryptoException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EcbSubstitution extends Substitution{
    private final String regExp = "[ed]{1}ecb\\([\\dA-F]+,[\\dA-F]{64}\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static EcbSubstitution instance = new EcbSubstitution();

    private EcbSubstitution(){
    }

    public static EcbSubstitution getInstance(String line){
        instance.reset(line);
        return instance;
    }

    public static Substitution getInstance(){
        return instance;
    }

    @Override
    protected String execute(String message) {
        matcher.reset();
        boolean toEncrypt = message.charAt(0) == 'e';
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        byte[] text = Utilities.stringToBytes(textAndKey[0]);
        byte[] key = Utilities.stringToBytes(textAndKey[1]);
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        try {
            return Utilities.bytesToString(cipher.ecb(text, key, toEncrypt));
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
