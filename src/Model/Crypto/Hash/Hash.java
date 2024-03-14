package Model.Crypto.Hash;

import Model.XToY;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

import java.util.HashMap;

public class Hash {
    private static final HashMap<String, DigestAlg> typeDigestMap;
    private static final DigestDirector director;

    static {
        typeDigestMap = new HashMap<>();
        typeDigestMap.put("hash094", DigestAlg.HASH_94);
        typeDigestMap.put("hash256", DigestAlg.HASH_2012_256);
        typeDigestMap.put("hash512", DigestAlg.HASH_2012_512);

        director = new DigestDirector();
    }

    public static String execute(String message, String alg){
        GOSTDigestFactory factory = null;
        try {
            factory = director.getFactory(typeDigestMap.get(alg));
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
        ExtendedDigest digest = factory.create();

        byte[] digest_ = new byte[digest.getByteLength()];
        byte[] m = XToY.stringToBytes(message);
        hashCalculate(digest, m, digest_);
        return XToY.bytesToString(Arrays.reverse(digest_));
    }

    private static void hashCalculate(ExtendedDigest hash, byte[] message, byte[] digest) {
        if (hash == null)
            return;

        hash.update(message, 0, message.length);
        hash.doFinal(digest, 0);
    }
}