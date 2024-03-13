package Model;

import java.math.BigInteger;

public class Calculator {

    private static BigInteger A;
    private static BigInteger B;

        public static String add(String a, String b){
        init(a, b);
        String result = String.format("%02X", A.add(B));
        result = getResult(a, b, result);
        return result;
    }

    public static String sub(String a, String b){
        init(a, b);
        String result = String.format("%02X", A.subtract(B));
        result = getResult(a, b, result);
        return result;
    }

    public static String mul(String a, String b){
        init(a, b);
        String result = String.format("%02X", A.multiply(B));
        result = getResult(a, b, result);
        return result;
    }

    public static String del(String a, String b){
        init(a, b);
        String result = String.format("%02X", A.divide(B));
        result = getResult(a, b, result);
        return result;
    }

    public static String mod(String a, String b){
        init(a, b);
        String result = String.format("%02X", A.mod(B));
        result = getResult(a, b, result);
        return result;
    }

    private static int getReturnLength(String a, String b, String c){
        int length;
        if(a.length() >= b.length()){
            if (a.length() >= c.length())
                length = a.length();
            else
                length = c.length();
        }else {
            if (b.length() >= c.length())
                length = b.length();
            else
                length = c.length();
        }
        return length;
    }

    private static String getResult(String a, String b, String result) {
        int length = getReturnLength(a, b, result);
        int count = Math.abs(result.length() - length);
        StringBuilder resultBuilder = new StringBuilder(result);
        while (count != 0) {
            resultBuilder.insert(0, "0");
            count--;
        }
        result = resultBuilder.toString();
        return result;
    }

    private static void init(String a, String b){
        A = new BigInteger(a, 16);
        B = new BigInteger(b, 16);
    }
}
