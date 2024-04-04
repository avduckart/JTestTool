package Model;

import javax.smartcardio.CommandAPDU;
import java.util.Arrays;

public class APDUCommand {

    public static CommandAPDU create(String apduString) {
        return new CommandAPDU(Utilities.stringToBytes(apduString));
    }

     public static String view(CommandAPDU apduCommand) {

         int headLength = (apduCommand.getNc() == 0) ? 4 : 5;

         StringBuilder strBuilder = new StringBuilder("  Send command -->\n\t");
         byte[] bytes = apduCommand.getBytes();
         int bodyLength = bytes.length - headLength;

         for (int i = 0; i < headLength; i++)
            strBuilder.append(String.format("%02X ", bytes[i]));

        int bodyStringsCount = (bodyLength % 16 == 0) ? (bodyLength / 16) : (1 + bodyLength / 16);

        for (int j = 0; j < bodyStringsCount; j++) {
            strBuilder.append("\n\t");
            for (int i = 0; i < ((bodyLength > 16) ? 16 : bodyLength); i++)
                strBuilder.append(String.format("%02X ", bytes[headLength + j * 16 + i]));
            bodyLength = bodyLength - 16;
        }

        return strBuilder.toString();

        //return Utilities.bytesToString(apduCommand.getBytes());
    }
}
