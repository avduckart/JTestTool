import javax.smartcardio.ResponseAPDU;

public class APDUResponse {
    private static final int STRING_LENGTH = 16;

    public static String view(ResponseAPDU apdu) {
        if (apdu == null)
            return "APDU-RESPONSE IS NULL";

        StringBuilder strBuilder = new StringBuilder("  Response <--\n");
        byte[] data = apdu.getData();

        for (int i = 0; i < apdu.getNr(); i++) {
            if(i % STRING_LENGTH == 0)
                strBuilder.append("\n\t");
            strBuilder.append(String.format("%02X ", data[i]));
        }

        strBuilder.append("\n\t");
        strBuilder.append(String.format("%02X ", apdu.getSW1()));
        strBuilder.append(String.format("%02X ", apdu.getSW2()));

        return strBuilder.toString();
    }

    public static String toString(ResponseAPDU apdu) {
        if (apdu == null)
            return "APDU-RESPONSE IS NULL";

        StringBuilder strBuilder = new StringBuilder();
        byte[] bytes = apdu.getBytes();
        for (byte b : bytes)
            strBuilder.append(String.format("%02X", b));

        return strBuilder.toString();
    }
}
