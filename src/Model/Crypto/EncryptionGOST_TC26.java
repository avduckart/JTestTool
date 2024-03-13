package Model.Crypto;

import Model.XToY;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.params.KeyParameter;



import java.util.Arrays;
import java.util.Hashtable;

public class EncryptionGOST_TC26 implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey = null;
    private boolean forEncryption;
    private byte[] S;

    private static final byte[] Sbox_Z = {
            0xC,0x4,0x6,0x2,0xA,0x5,0xB,0x9,0xE,0x8,0xD,0x7,0x0,0x3,0xF,0x1,
            0x6,0x8,0x2,0x3,0x9,0xA,0x5,0xC,0x1,0xE,0x4,0x7,0xB,0xD,0x0,0xF,
            0xB,0x3,0x5,0x8,0x2,0xF,0xA,0xD,0xE,0x1,0x7,0x4,0xC,0x9,0x6,0x0,
            0xC,0x8,0x2,0x1,0xD,0x4,0xF,0x6,0x7,0x0,0xA,0x5,0x3,0xE,0x9,0xB,
            0x7,0xF,0x5,0xA,0x8,0x1,0x6,0xD,0x0,0x9,0x3,0xE,0xB,0x4,0x2,0xC,
            0x5,0xD,0xF,0x6,0x9,0x2,0xC,0xA,0xB,0x7,0x8,0x1,0x4,0x3,0xE,0x0,
            0x8,0xE,0x2,0x5,0x6,0x9,0x1,0xC,0xF,0x4,0xB,0x0,0xD,0xA,0x3,0x7,
            0x1,0x7,0xE,0xD,0x0,0x5,0x8,0x3,0x4,0xF,0xA,0x6,0x9,0xC,0xB,0x2
    };
    private static Hashtable sBoxes = new Hashtable();

    public EncryptionGOST_TC26() {
        S = Sbox_Z;
    }

    static {
        sBoxes.put("tc26-param-Z", Sbox_Z);
    }


    public String dcfb(String crypt, String IV, String key) throws CryptoException {
        if (IV.length() != 16 || key.length() != 64)
            throw new CryptoException();
        byte[] in = XToY.stringToBytes(crypt);
        byte[] iv = XToY.stringToBytes(IV);
        byte[] keyArr = XToY.stringToBytes(key);
        byte[] gamma;
        byte[] out = new byte[in.length];
        byte[] curIV;
        curIV = iv;
        byte[][] blocks = new byte[in.length / BLOCK_SIZE][in.length % BLOCK_SIZE];
        for (int i = 0; i < blocks.length; i++)
            blocks[i] = Arrays.copyOfRange(in, BLOCK_SIZE * i, BLOCK_SIZE * (i + 1));

        for (int i = 0; i < blocks.length; i++) {
            gamma = eecb(curIV, keyArr);
            for (int j = 0; j < 8; j++) {
                curIV[j] = out[j + 8 * i] = (byte) (blocks[i][j] ^ gamma[j]);
            }
        }
        return XToY.bytesToString(out);
    }


    public String ecfb(String message, String IV, String key) throws CryptoException {
        if (IV.length() != 16 || key.length() != 64)
            throw new CryptoException();
        byte[] in = XToY.stringToBytes(message);
        byte[] iv = XToY.stringToBytes(IV);
        byte[] keyArr = XToY.stringToBytes(key);
        byte[] gamma;
        byte[] out = new byte[in.length];
        byte[] curIV;
        curIV = iv;
        byte[][] blocks = new byte[in.length/BLOCK_SIZE][in.length%BLOCK_SIZE];
        for (int i = 0; i < blocks.length; i++)
            blocks[i] = Arrays.copyOfRange(in, BLOCK_SIZE*i, BLOCK_SIZE*(i+1));

        for (int i = 0; i < blocks.length; i++){
            gamma = eecb(curIV, keyArr);
            for (int j = 0; j < 8; j++) {
                curIV[j] = out[j+8*i] = (byte)(blocks[i][j]^gamma[j]);
            }
        }
        return XToY.bytesToString(out);
    }

    private byte[] xorArr(byte[] arr, byte[] brr){
        if(arr.length != brr.length)
            return null;
        for (int i = 0; i < arr.length; i++)
            arr[i] = (byte) (arr[i] ^ brr[i]);
        return arr;
    }

    public byte[] eecb(byte[] text, byte[] key) throws CryptoException {
        if (text.length%BLOCK_SIZE != 0 || key.length != 32)
            throw new CryptoException();
        byte[] out = new byte[text.length];
        this.init(true, new KeyParameter(key));
        for(int i = 0; i < text.length/BLOCK_SIZE; i++) {
            this.processBlock(text, i*BLOCK_SIZE, out, i*BLOCK_SIZE);
        }
        return out;
    }

    public byte[] decb(byte[] text, byte[] key) throws CryptoException {
        if (text.length%BLOCK_SIZE != 0 || key.length != 32)
            throw new CryptoException();
        byte[] out = new byte[text.length];
        this.init(false, new KeyParameter(key));
        for(int i = 0; i < text.length/BLOCK_SIZE; i++) {
            this.processBlock(text, i*BLOCK_SIZE, out, i*BLOCK_SIZE);
        }
        return out;
    }

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

    public void init(boolean forEncryption, CipherParameters params) {
        this.workingKey = this.generateWorkingKey(forEncryption, ((KeyParameter)params).getKey());
    }

    public String getAlgorithmName() {
        return "GOST_28147-89";
    }

    public int getBlockSize() {
        return 8;
    }

    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.workingKey == null) {
            throw new IllegalStateException("GOST28147 engine not initialised");
        } else if (inOff + BLOCK_SIZE > in.length) {
            throw new DataLengthException("input buffer too short");
        } else if (outOff + BLOCK_SIZE > out.length) {
            throw new OutputLengthException("output buffer too short");
        } else {
            this.GOST28147Func(this.workingKey, in, inOff, out, outOff);
            return BLOCK_SIZE;
        }
    }

    public void reset() {
    }

    private int[] generateWorkingKey(boolean forEncryption, byte[] key) {
        this.forEncryption = forEncryption;
        if (key.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        } else {
            int[] subKey = new int[8];
            for(int i = 0; i != 8; ++i)
                subKey[i] = this.bytesToInt(key, i * 4);
            return subKey;
        }
    }

    private int GOST28147_mainStep(int block, int key) {
        int line = key + block;
        int sLine = this.S[0 + (line >> 0 & 15)] << 0;
        sLine += this.S[16 + (line >> 4 & 15)] << 4;
        sLine += this.S[32 + (line >> 8 & 15)] << 8;
        sLine += this.S[48 + (line >> 12 & 15)] << 12;
        sLine += this.S[64 + (line >> 16 & 15)] << 16;
        sLine += this.S[80 + (line >> 20 & 15)] << 20;
        sLine += this.S[96 + (line >> 24 & 15)] << 24;
        sLine += this.S[112 + (line >> 28 & 15)] << 28;
        return sLine << 11 | sLine >>> 21;
    }

    private void GOST28147Func(int[] workingKey, byte[] in, int offsetIn, byte[] result, int offsetResult) {
        int n1 = this.bytesToInt(in, offsetIn);
        int n2 = this.bytesToInt(in, offsetIn + 4);
        int tmp;
        int subKeyNumber;
        int roundKeyNumber;
        if (this.forEncryption) {
            for(subKeyNumber = 0; subKeyNumber < 3; ++subKeyNumber)
                forwardKeyTransform(n1, n2, workingKey, 8);
            invertKeyTransform(n1, n2, workingKey, 8);
        } else {
            forwardKeyTransform(n1, n2, workingKey, 8);
            for(subKeyNumber = 0; subKeyNumber < 3; ++subKeyNumber) {
                for(roundKeyNumber = 7; roundKeyNumber >= 0 && (subKeyNumber != 2 || roundKeyNumber != 0); --roundKeyNumber) {
                    tmp = n1;
                    n1 = n2 ^ this.GOST28147_mainStep(n1, workingKey[roundKeyNumber]);
                    n2 = tmp;
                }
            }
        }
        n2 ^= this.GOST28147_mainStep(n1, workingKey[0]);
        this.intToBytes(n1, result, offsetResult);
        this.intToBytes(n2, result, offsetResult + 4);
    }

    private void forwardKeyTransform(int n1, int n2, int[] workingKey, int count){
        int tmp;
        for(int roundKeyNumber = 0; roundKeyNumber < count; ++roundKeyNumber) {
            tmp = n1;
            n1 = n2 ^ this.GOST28147_mainStep(n1, workingKey[roundKeyNumber]);
            n2 = tmp;
        }
    }

    private void invertKeyTransform(int n1, int n2, int[] workingKey, int count){
        int tmp;
        for(int roundKeyNumber = count-1; roundKeyNumber >= 0; --roundKeyNumber) {
            tmp = n1;
            n1 = n2 ^ this.GOST28147_mainStep(n1, workingKey[roundKeyNumber]);
            n2 = tmp;
        }
    }

    private int bytesToInt(byte[] bytes, int offset) {
        return (bytes[offset + 3] << 24 & -16777216) + (bytes[offset + 2] << 16 & 16711680) + (bytes[offset + 1] << 8 & '\uff00') + (bytes[offset] & 255);
    }

    private void intToBytes(int intCount, byte[] bytes, int offset) {
        bytes[offset + 3] = (byte)(intCount >>> 24);
        bytes[offset + 2] = (byte)(intCount >>> 16);
        bytes[offset + 1] = (byte)(intCount >>> 8);
        bytes[offset] = (byte)intCount;
    }
}
