package Model.Crypto.Digest;

import Model.XToY;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

import java.util.HashMap;

public class Digest {
    public static final HashMap<String, DigestAlg> typeDigestMap;
    private final ExtendedDigest digest;

    static {
        typeDigestMap = new HashMap<>();
        typeDigestMap.put("hash094", DigestAlg.HASH_94);
        typeDigestMap.put("hash256", DigestAlg.HASH_2012_256);
        typeDigestMap.put("hash512", DigestAlg.HASH_2012_512);
    }

    public Digest(DigestAlg alg){
        digest = DigestDirector.getFactory(alg).create();
    }

    public Digest(String alg){
        this(typeDigestMap.get(alg));
    }

    public String execute(String message){
        byte[] hash = new byte[digest.getByteLength()];
        byte[] msg = XToY.stringToBytes(message);
        hashCalculate(msg, hash);
        return XToY.bytesToString(Arrays.reverse(hash));
    }

    private void hashCalculate(byte[] message, byte[] hash) {
        digest.update(message, 0, message.length);
        digest.doFinal(hash, 0);
    }
}