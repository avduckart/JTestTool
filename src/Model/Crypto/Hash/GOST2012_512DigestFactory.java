package Model.Crypto.Hash;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;

public class GOST2012_512DigestFactory implements GOSTDigestFactory{
    @Override
    public ExtendedDigest create() {
        return new GOST3411_2012_512Digest();
    }
}
