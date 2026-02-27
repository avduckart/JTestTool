package JTestCrypto.JTestPbkdf2;

import JTestCrypto.JTestHMac.JTestHMac;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;

public class JTestPBKDF2 {
    private static final int blockSize = 64;
    private static final JTestHMac hmac = new JTestHMac(new GOST3411_2012_512Digest());

    public static String execute(String password, String salt, String c, String dkLen){
        int count = Integer.parseInt(c);
        int length = Integer.parseInt(dkLen);
        int blockCount = (length + (blockSize - length % blockSize) % blockSize) / blockSize;
        byte[] p = Utils.stringToBytes(password);
        byte[] u, t, s;
        StringBuilder K = new StringBuilder();
        for (int i = 0; i < blockCount; i++){
            s = Utils.stringToBytes(String.format("%s%02X", salt, i));
            u = hmac.hmac(p, s);
            t = u;
            for (int j = 0; j < count; j++){
                u = hmac.hmac(p,u);
                t = xorArr(t,u);
            }
            K.append(Utils.bytesToString(t));
        }
        return K.delete(64*blockCount-length, 64*blockCount).toString();
    }

    private static byte[] xorArr(byte[] arr, byte[] brr){
        if(arr.length != brr.length)
            return null;
        for (int i = 0; i < arr.length; i++)
            arr[i] ^= brr[i];
        return arr;
    }
}
