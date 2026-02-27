import javax.smartcardio.ResponseAPDU;

public class APDUResponse{

    public static String view(ResponseAPDU response){
            StringBuilder strBuilder = new StringBuilder("");
        if(response != null) {
            int bodyStringsCount;
            int RESULT_LENGTH = 2;
            int STRING_LENGTH = 16;
            byte[] bytes = response.getBytes();
            int length = bytes.length;
            int bodyLength = length - RESULT_LENGTH;

            strBuilder.append("  Response <--\n");
            bodyStringsCount = (bodyLength % STRING_LENGTH == 0) ? bodyLength / STRING_LENGTH : (1 + bodyLength / STRING_LENGTH);
            for (int j = 0; j < bodyStringsCount; j++) {
                strBuilder.append("\t");
                for (int i = 0; i < ((bodyLength > STRING_LENGTH) ? STRING_LENGTH : bodyLength); i++)
                    strBuilder.append(String.format("%02X ", bytes[j * STRING_LENGTH + i]));
                strBuilder.append("\n");
                bodyLength = bodyLength - STRING_LENGTH;
            }
            strBuilder.append("\t");
            for (int i = RESULT_LENGTH; i > 0; i--)
                strBuilder.append(String.format("%02X ", bytes[length - i]));
        }
        else
            strBuilder.append("APDU-RESPONSE IS NULL");
        return strBuilder.toString();
    }

    public static String toString(ResponseAPDU response){
        StringBuilder strBuilder = new StringBuilder("");
        if(response != null) {
            byte[] bytes = response.getBytes();
            for (byte b : bytes)
                strBuilder.append(String.format("%02X", b));
        }
        else
            strBuilder.append("APDU-RESPONSE IS NULL");
        return strBuilder.toString();
    }
}
