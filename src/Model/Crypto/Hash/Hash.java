package Model.Crypto.Hash;

import Model.XToY;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

import java.util.HashMap;

public class Hash {
    public static final HashMap<String, DigestAlg> typeDigestMap;
    private static final DigestDirector director;
    private ExtendedDigest digest;

    static {
        typeDigestMap = new HashMap<>();
        typeDigestMap.put("hash094", DigestAlg.HASH_94);
        typeDigestMap.put("hash256", DigestAlg.HASH_2012_256);
        typeDigestMap.put("hash512", DigestAlg.HASH_2012_512);

        director = new DigestDirector();
    }

    public Hash(DigestAlg alg){
        digest = director.getFactory(alg).create();
    }

    public Hash(String alg){
        this(typeDigestMap.get(alg));
    }

    public String execute(String message, String alg){
        byte[] digest_ = new byte[digest.getByteLength()];
        byte[] m = XToY.stringToBytes(message);
        hashCalculate(digest, m, digest_);
        return XToY.bytesToString(Arrays.reverse(digest_));
    }

    private void hashCalculate(ExtendedDigest hash, byte[] message, byte[] digest) {
        if (hash == null)
            return;

        hash.update(message, 0, message.length);
        hash.doFinal(digest, 0);
    }
}