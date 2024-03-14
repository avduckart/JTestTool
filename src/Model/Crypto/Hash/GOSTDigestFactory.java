package Model.Crypto.Hash;

import org.bouncycastle.crypto.ExtendedDigest;

public interface GOSTDigestFactory {
    ExtendedDigest create();
}
