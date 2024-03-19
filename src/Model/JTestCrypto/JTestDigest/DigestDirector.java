package Model.JTestCrypto.JTestDigest;

public class DigestDirector {
    public static GOSTDigestFactory getFactory(DigestAlg alg) {
        switch(alg){
            case HASH_94 :
                return new GOST94DigestFactory();
            case HASH_2012_256:
                return new GOST2012_256DigestFactory();
            default /* HASH_2012_512 */:
                return new GOST2012_512DigestFactory();
        }
    }
}
