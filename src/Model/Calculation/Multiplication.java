package Model.Calculation;

import java.math.BigInteger;

public class Multiplication implements Operation{
    @Override
    public String execute(String a, String b) {
        BigInteger res = new BigInteger(a).multiply(new BigInteger(b));
        return String.format("%02X", res);
    }
}
