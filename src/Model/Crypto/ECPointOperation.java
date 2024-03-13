package Model.Crypto;

import java.math.BigInteger;
import java.util.Arrays;
import Model.XToY;
import org.bouncycastle.math.ec.*;

public class ECPointOperation {

    private static final BigInteger X = new BigInteger("1", 16);
    private static final BigInteger Y = new BigInteger("8D91E471E0989CDA27DF505A453F2B7635294F2DDF23E3B122ACC99C9E9F1E14", 16);
    private static final BigInteger A = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD94", 16);
    private static final BigInteger B = new BigInteger("166", 16);
    private static final BigInteger P = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD97", 16);
    private static final BigInteger Q = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6C611070995AD10045841B09B761B893", 16);

    private static final ECCurve.Fp ecCurve = new ECCurve.Fp(P, A, B);
    private static final ECFieldElement.Fp G_x = new ECFieldElement.Fp(P, X);
    private static final ECFieldElement.Fp G_y = new ECFieldElement.Fp(P, Y);
    private static final ECPoint.Fp ecPoint = new ECPoint.Fp(ecCurve, G_x, G_y,false);


    public static void addPoints(byte[] multiplicand, short multiplicandOffset, byte[] multiplier, short multiplierOffset) {
        byte[] x1Array = new byte[32];
        byte[] y1Array = new byte[32];
        byte[] x2Array = new byte[32];
        byte[] y2Array = new byte[32];
        System.arraycopy(multiplicand, multiplicandOffset, x1Array, 0, 32);
        System.arraycopy(multiplicand, multiplicandOffset + 32, y1Array, 0, 32);
        System.arraycopy(multiplier, multiplierOffset, x2Array, 0, 32);
        System.arraycopy(multiplier, multiplierOffset + 32, y2Array, 0, 32);
        ECPoint ecPoint1 = ecCurve.createPoint(new BigInteger(1, x1Array), new BigInteger(1, y1Array));
        ECPoint ecPoint2 = ecCurve.createPoint(new BigInteger(1, x2Array), new BigInteger(1, y2Array));
        ecPoint1 = ecPoint1.add(ecPoint2);
        bigIntegerToArray(ecPoint1.normalize().getXCoord().toBigInteger(), multiplicand, multiplicandOffset, 32);
        bigIntegerToArray(ecPoint1.normalize().getYCoord().toBigInteger(), multiplicand, multiplicandOffset + 32, 32);
    }


    private static void bigIntegerToArray(BigInteger bi, byte[] array, int aOffset, int aLength) {
        byte[] res = bi.toByteArray();
        Arrays.fill(array, aOffset, aOffset + aLength, (byte)0);
        int resOffset = 0;
        int resLength = res.length;
        if (resLength > aLength) {
            resOffset = resLength - aLength;
            resLength = aLength;
        } else
            aOffset += aLength - resLength;
        System.arraycopy(res, resOffset, array, aOffset, resLength);
    }

    public static void multiplyPoint(byte[] point, short pointOffset, byte[] mul, short multiplierOffset) {
        byte[] xArray = new byte[32];
        byte[] yArray = new byte[32];
        byte[] kArray = new byte[32];
        System.arraycopy(point, pointOffset, xArray, 0, 32);
        System.arraycopy(point, pointOffset + 32, yArray, 0, 32);
        System.arraycopy(mul, multiplierOffset, kArray, 0, 32);
        ECPoint resPoint = multiply(ecPoint, new BigInteger("2", 16));
        bigIntegerToArray(resPoint.normalize().getXCoord().toBigInteger(), point, pointOffset, 32);
        bigIntegerToArray(resPoint.normalize().getYCoord().toBigInteger(), point, pointOffset + 32, 32);
    }

    private static ECPoint multiply(ECPoint point, BigInteger alpha) {
        int signum = alpha.signum();
        if (signum != 0 && !point.isInfinity()) {
            ECPoint multiplication = multiplyPositive(point, alpha.abs());
            ECPoint result = signum > 0 ? multiplication : multiplication.negate();
            return result;
        } else
            return point.getCurve().getInfinity();
    }

    private static ECPoint multiplyPositive(ECPoint point, BigInteger alpha) {
        ECPoint norm = point.normalize();
        ECPoint neg = norm.negate();
        ECPoint multResult = norm;
        int bitLength = alpha.bitLength();
        int lowestSetBitIndex = alpha.getLowestSetBit();
        int count = bitLength;

        while(true) {
            --count;
            if (count <= lowestSetBitIndex)
                while(true) {
                    --lowestSetBitIndex;
                    if (lowestSetBitIndex < 0)
                        return multResult;
                    multResult = multResult.twice();
                }
            multResult = multResult.twicePlus(alpha.testBit(count) ? norm : neg);
        }
    }

    public static String getMultipledPoint(String point, String multiplier){
        byte[] pointB = XToY.stringToBytes(point);
        byte[] mul = XToY.stringToBytes(multiplier);
        multiplyPoint(pointB, (short)0, mul, (short)0);
        return XToY.bytesToString(pointB);
    }

    public static String diverseKey(String UKM, String CK, String OK){
        BigInteger ukm = new BigInteger(UKM,16);
        BigInteger ck = new BigInteger(CK,16);
        String multiplier = ukm.multiply(ck).mod(Q).toString(16);
        return getMultipledPoint(OK, multiplier);
    }

    public static String sumPoint(String point1, String point2) {
        byte[] A = XToY.stringToBytes(point1);
        byte[] B = XToY.stringToBytes(point2);
        addPoints(A, (short)0, B, (short)0);
        return XToY.bytesToString(A);
    }
}
