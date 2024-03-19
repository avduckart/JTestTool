package Model.Calculation;

import java.math.BigInteger;

public class Substraction implements Operation{
    @Override
    public String execute(String a, String b) {
        BigInteger res = new BigInteger(a).subtract(new BigInteger(b));
        return String.format("%02X", res);
    }
}
