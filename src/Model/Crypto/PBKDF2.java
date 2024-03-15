package Model.Crypto;

import Model.XToY;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;

public class PBKDF2 {
    public static String pbkdf2(String password, String salt, String c,String dkLen){
        int count = Integer.parseInt(c);
        int len = Integer.parseInt(dkLen);
        int n = (len%64 == 0) ? len/64 : len/64 + 1;
        byte[] p = XToY.stringToBytes(password);
        byte[] u, t, s;
        HMAC hmacStandart = new HMAC(new GOST3411_2012_512Digest());
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
