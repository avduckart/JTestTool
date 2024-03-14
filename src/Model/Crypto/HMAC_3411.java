package Model.Crypto;

import Model.Crypto.Hash.DigestAlg;
import Model.Crypto.Hash.DigestDirector;
import Model.Crypto.Hash.GOSTDigestFactory;
import Model.XToY;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import java.util.HashMap;


public class HMAC_3411 extends HMac {
    private static final int B = 32;
    private static final int L = 32;
    private static final HashMap<String, DigestAlg> typeDigestMap;
    private static final DigestDirector director;

    static {
        typeDigestMap = new HashMap<>();
        typeDigestMap.put("hash094", DigestAlg.HASH_94);
        typeDigestMap.put("hash256", DigestAlg.HASH_2012_256);
        typeDigestMap.put("hash512", DigestAlg.HASH_2012_512);

        director = new DigestDirector();
    }

    private HMAC_3411(Digest digest) {
        super(digest);
    }

    public static String execute(String message, String key, String alg){
        GOSTDigestFactory factory = null;
        try {
            factory = director.getFactory(typeDigestMap.get(alg));
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
        ExtendedDigest digest = factory.create();

        return getHMAC(message, key, digest);
    }

    private static String getHMAC(String message, String key, ExtendedDigest digest){
        HMAC_3411 hmac = new HMAC_3411(digest);
        byte[] m = XToY.stringToBytes(message);
        byte[] keyArr = XToY.stringToBytes(key);
        return XToY.bytesToString(hmac.hmac(m, keyArr));
    }

    private  byte[] hmac(byte[] text, byte[] key){
        byte[] result = new byte[64];
        init(new KeyParameter(key));
        update(text, 0, text.length);
        doFinal(result, 0);
        return result;
    }

    public static String pbkdf2(String password, String salt, String c,String dkLen){
        int count = Integer.parseInt(c);
        int len = Integer.parseInt(dkLen);
        int n = (len%64 == 0) ? len/64 : len/64 + 1;
        byte[] p = XToY.stringToBytes(password);
        byte[] u, t, s;
        HMAC_3411 hmacStandart = new HMAC_3411(new GOST3411_2012_512Digest());
        StringBuilder K = new StringBuilder();
        for (int i = 0; i < n; i++){
            s = XToY.stringToBytes(String.format("%s%02X", salt, i));
            u = hmacStandart.hmac(p, s);
            t = u;
            for (int j = 0; j < count; j++){
                u = hmacStandart.hmac(p,u);
                t = xorArr(t,u);
            }
            K.append(XToY.bytesToString(t));
        }
        return K.delete(64*n-len, 64*n).toString();
    }

    private static byte[] xorArr(byte[] arr, byte[] brr){
        if(arr.length != brr.length)
            return null;
        for (int i = 0; i < arr.length; i++)
            arr[i] ^= brr[i];
        return arr;
    }
}
