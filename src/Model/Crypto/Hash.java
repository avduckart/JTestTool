package Model.Crypto;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.util.Arrays;
import Model.XToY;

public class Hash {
    private static final int HASH_94 =  1;
    private static final int HASH_2012_256 =  2;
    private static final int HASH_2012_512 =  3;

    public static String executeHash(String message, String alg){
        if("hash094".equals(alg))
            return getHash(message, HASH_94);
        else if("hash256".equals(alg))
            return getHash(message, HASH_2012_256);
        else if("hash512".equals(alg))
            return getHash(message,  HASH_2012_512);
        else return null;
    }

    private static String getHash(String message, int algId){
        int digestSize = 0;
        ExtendedDigest hashStandart = null;
        switch (algId){
            case HASH_94 :
                digestSize = 32;
                hashStandart = new GOST3411Digest();
                break;
            case HASH_2012_256 :
                digestSize = 32;
                hashStandart = new GOST3411_2012_256Digest();
                break;
            case HASH_2012_512 :
                digestSize = 64;
                hashStandart = new GOST3411_2012_512Digest();
                break;
        }
        byte[] digest = new byte[digestSize];
        byte[] m = XToY.stringToBytes(message);
        hashCalculate(hashStandart, m, digest);
        return XToY.bytesToString(Arrays.reverse(digest));
    }

    private static void hashCalculate(ExtendedDigest hash, byte[] message, byte[] digest) {
        if (hash != null) {
            hash.update(message, 0, message.length);
            hash.doFinal(digest, 0);
        }
    }
}
