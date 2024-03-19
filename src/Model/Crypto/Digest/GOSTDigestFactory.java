package Model.Crypto.Digest;

import org.bouncycastle.crypto.ExtendedDigest;

public interface GOSTDigestFactory {
    ExtendedDigest create();
}
