package Model.Crypto.Hash;

import org.bouncycastle.crypto.CryptoException;

public class DigestDirector {
    public GOSTDigestFactory getFactory(DigestAlg alg) throws CryptoException {
        switch(alg){
            case HASH_94 :
                return new GOST94DigestFactory();
            case HASH_2012_256:
                return new GOST2012_256DigestFactory();
            case HASH_2012_512:
                return new GOST2012_512DigestFactory();
            default:
                throw new CryptoException();
        }
    }
}
