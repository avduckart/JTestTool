package JTestCrypto.JTestHMac;

import JTestCrypto.JTestDigest.DigestAlg;
import JTestCrypto.JTestDigest.DigestDirector;
import JTestCrypto.JTestDigest.JTestDigest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;


public class JTestHMac {
    private HMac hmac;

    public HMac get(){
        return hmac;
    }

    public void set(HMac hmac){
        this.hmac = hmac;
    }

    public JTestHMac(Digest digest) {
        hmac = new HMac(digest);
    }

    public JTestHMac(DigestAlg alg) {
        this(DigestDirector.getFactory(alg).create());
    }

    public JTestHMac(String digest) {
        this(JTestDigest.typeDigestMap.get(digest));
    }

    public String execute(String message, String key){
        byte[] msg = Utils.stringToBytes(message);
        byte[] keyArr = Utils.stringToBytes(key);
        return Utils.bytesToString(hmac(msg, keyArr));
    }

    public byte[] hmac(byte[] text, byte[] key){
        byte[] result = new byte[64];
        hmac.init(new KeyParameter(key));
        hmac.update(text, 0, text.length);
        hmac.doFinal(result, 0);
        return result;
    }
}
