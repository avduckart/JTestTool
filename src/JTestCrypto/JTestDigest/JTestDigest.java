package JTestCrypto.JTestDigest;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

import java.util.HashMap;

public class JTestDigest {
    public static final HashMap<String, DigestAlg> typeDigestMap;
    private final ExtendedDigest digest;

    static {
        typeDigestMap = new HashMap<>();
        typeDigestMap.put("094", DigestAlg.HASH_94);
        typeDigestMap.put("256", DigestAlg.HASH_2012_256);
        typeDigestMap.put("512", DigestAlg.HASH_2012_512);
    }

    public JTestDigest(DigestAlg alg){
        digest = DigestDirector.getFactory(alg).create();
    }

    public JTestDigest(String alg){
        this(typeDigestMap.get(alg));
    }

    public String execute(String message){
        byte[] hash = new byte[digest.getByteLength()];
        byte[] msg = Utils.stringToBytes(message);
        hashCalculate(msg, hash);
        return Utils.bytesToString(Arrays.reverse(hash));
    }

    private void hashCalculate(byte[] message, byte[] hash) {
        digest.update(message, 0, message.length);
        digest.doFinal(hash, 0);
    }
}