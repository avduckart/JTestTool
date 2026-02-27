package JTestCrypto;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;


import java.util.Arrays;

public class EncryptionGOST_TC26 extends GOST28147Engine {
    protected static final int BLOCK_SIZE = 8;

    {
        init(true, new ParametersWithSBox(null, GOST28147Engine.getSBox("Param-Z")));
    }

    public byte[] cfb(byte[] in, byte[] IV, byte[] key) throws CryptoException {
        if (IV.length != 8 || key.length != 32)
            throw new CryptoException();
        byte[] gamma;
        byte[] out = new byte[in.length];
        byte[][] blocks = new byte[in.length/BLOCK_SIZE][in.length%BLOCK_SIZE];

        for (int i = 0; i < blocks.length; i++)
            blocks[i] = Arrays.copyOfRange(in, BLOCK_SIZE*i, BLOCK_SIZE*(i+1));
        for (int i = 0; i < blocks.length; i++){
            gamma = ecb(IV, key, true);
            for (int j = 0; j < 8; j++)
                IV[j] = out[j+8*i] = (byte)(blocks[i][j]^gamma[j]);
        }
        return out;
    }

    public byte[] ecb(byte[] text, byte[] key, Boolean forEncrypt) throws CryptoException {
        if (text.length%BLOCK_SIZE != 0 || key.length != 32 || forEncrypt == null)
            throw new CryptoException();
        byte[] out = new byte[text.length];
        init(forEncrypt, new KeyParameter(key));
        for(int i = 0; i < text.length/BLOCK_SIZE; i++)
            processBlock(text, i*BLOCK_SIZE, out, i*BLOCK_SIZE);
        return out;
    }

/*
    public byte[] imit(byte[] text, byte[] key, int length) throws CryptoException {
        if (text.length % BLOCK_SIZE != 0 || key.length != 32 || length > BLOCK_SIZE || length < 1)
            throw new CryptoException();
        byte[] out = new byte[8];
        byte[] result = new byte[length];
        this.init(true, new KeyParameter(key));
        int n1 = 0, n2 = 0;
        for (int i = 0; i < text.length / BLOCK_SIZE; i++) {
            n1 ^= this.bytesToInt(text, i * BLOCK_SIZE);
            n2 ^= this.bytesToInt(text, i * BLOCK_SIZE + 4);
            forwardKeyTransform(n1, n2, this.workingKey, 8);
            forwardKeyTransform(n1, n2, this.workingKey, 8);
        }
        n2 ^= this.GOST28147_mainStep(n1, workingKey[0]);
        this.intToBytes(n1, out, 0);
        this.intToBytes(n2, out, 4);
        Arrays.copyOfRange(result,out.length-length, out.length);
        return result;
    }
    */
}
