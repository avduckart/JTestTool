package Model.ScriptScanner;

import Model.JTestCrypto.JTestDigest.JTestDigest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HashSubstitution extends Substitution{
    private final String regExp = "hash(094|256|512)\\([\\dA-F]+\\)";
    private final Matcher matcher = Pattern.compile(regExp).matcher("");
    private final static Substitution instance = new HashSubstitution();

    private HashSubstitution(){
    }

    @Override
    protected String execute(String line){
        matcher.reset();
        String hashAlg = line.substring(4,7);
        String message = extractBtwBrackets(line);
        JTestDigest digest = new JTestDigest(JTestDigest.typeDigestMap.get(hashAlg));

        return digest.execute(message);
    }
}
