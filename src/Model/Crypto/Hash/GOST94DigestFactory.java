package Model.Crypto.Hash;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.GOST3411Digest;

public class GOST94DigestFactory implements GOSTDigestFactory{
    @Override
    public ExtendedDigest create() {
        return new GOST3411Digest();
    }
}
