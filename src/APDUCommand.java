import javax.smartcardio.CommandAPDU;

public class APDUCommand {
    private static final int STRING_LENGTH = 16;

    public CommandAPDU create(String apduString) {
        return new CommandAPDU(Utilities.stringToBytes(apduString));
    }

     public static String view(final CommandAPDU apdu) {
         StringBuilder strBuilder = new StringBuilder("  Send command -->\n\t");
         strBuilder.append(String.format("%02X ", apdu.getCLA()));
         strBuilder.append(String.format("%02X ", apdu.getINS()));
         strBuilder.append(String.format("%02X ", apdu.getP1()));
         strBuilder.append(String.format("%02X ", apdu.getP2()));

         final byte[] data = apdu.getData();

         for (int i = 0; i < data.length; i++) {
            if(i % STRING_LENGTH == 0)
                strBuilder.append("\n\t");
            strBuilder.append(String.format("%02X ", data[i]));
        }

        return strBuilder.toString();
    }
}
