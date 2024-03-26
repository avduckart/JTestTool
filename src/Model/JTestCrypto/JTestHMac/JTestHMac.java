package Model.JTestCrypto.JTestHMac;

import Model.JTestCrypto.JTestDigest.DigestAlg;
import Model.JTestCrypto.JTestDigest.DigestDirector;
import Model.JTestCrypto.JTestDigest.JTestDigest;
import Model.Utilities;
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
        byte[] msg = Utilities.stringToBytes(message);
        byte[] keyArr = Utilities.stringToBytes(key);
        return Utilities.bytesToString(hmac(msg, keyArr));
    }

    public byte[] hmac(byte[] text, byte[] key){
        byte[] result = new byte[64];
        hmac.init(new KeyParameter(key));
        hmac.update(text, 0, text.length);
        hmac.doFinal(result, 0);
        return result;
    }
}
