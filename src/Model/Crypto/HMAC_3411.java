package Model.Crypto;

import Model.XToY;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;

import javax.crypto.SecretKeyFactory;
import java.lang.reflect.Array;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;


public class HMAC_3411 extends HMac {
    private static final int B = 32;
    private static final int L = 32;
    private static final int HMAC_94 =  1;
    private static final int HMAC_2012_256 =  2;
    private static final int HMAC_2012_512 =  3;

    private HMAC_3411(Digest digest) {
        super(digest);
    }

    public static String executeHmac(String message, String key, String alg){
        if("hmac094".equals(alg))
            return getHMAC(message, key, HMAC_94);
        else if("hmac256".equals(alg))
            return getHMAC(message, key, HMAC_2012_256);
        else if("hmac512".equals(alg))
            return getHMAC(message, key, HMAC_2012_512);
        else return null;
    }

    private static String getHMAC(String message, String key, int algId){
        HMAC_3411 hmacStandart = null;
        switch (algId){
            case HMAC_94 :
                hmacStandart = new HMAC_3411(new GOST3411Digest());
                break;
            case HMAC_2012_256 :
                hmacStandart = new HMAC_3411(new GOST3411_2012_256Digest());
                break;
            case HMAC_2012_512 :
                hmacStandart = new HMAC_3411(new GOST3411_2012_512Digest());
                break;
        }
        byte[] m = XToY.stringToBytes(message);
        byte[] keyArr = XToY.stringToBytes(key);
        return XToY.bytesToString(hmacStandart.hmacCalculate(m, keyArr));
    }

    private  byte[] hmacCalculate(byte[] text, byte[] key){
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
            u = hmacStandart.hmacCalculate(p, s);
            t = u;
            for (int j = 0; j < count; j++){
                u = hmacStandart.hmacCalculate(p,u);
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
            arr[i] = (byte) (arr[i] ^ brr[i]);
        return arr;
    }
}
