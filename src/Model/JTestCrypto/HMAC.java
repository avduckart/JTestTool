package Model.JTestCrypto;

import Model.JTestCrypto.JTestDigest.DigestAlg;
import Model.JTestCrypto.JTestDigest.DigestDirector;
import Model.JTestCrypto.JTestDigest.JTestDigest;
import Model.XToY;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;


public class HMAC {
    private HMac hmac;

    public HMAC(org.bouncycastle.crypto.Digest digest) {
        hmac = new HMac(digest);
    }

    public HMAC(DigestAlg alg) {
        this(DigestDirector.getFactory(alg).create());
    }

    public HMAC(String digest) {
        this(JTestDigest.typeDigestMap.get(digest));
    }

    public String hmac(String message, String key){
        byte[] m = XToY.stringToBytes(message);
        byte[] keyArr = XToY.stringToBytes(key);
        return XToY.bytesToString(hmac(m, keyArr));
    }

    public byte[] hmac(byte[] text, byte[] key){
        byte[] result = new byte[64];
        hmac.init(new KeyParameter(key));
        hmac.update(text, 0, text.length);
        hmac.doFinal(result, 0);
        return result;
    }
}
