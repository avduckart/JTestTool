package Model;

import javax.smartcardio.CommandAPDU;

class APDUCommand {

    /*
        Преобразование APDU-команды строки в массив байтов
     */
    public static int[] getBytes(String apduString){
        int[] bytes;                       // выходной массив
        int bodyLength;                           // длина тела APDU-команды
        int APDU_HEAD_LENGTH;                 // длина заглавия APDU-команды

        if(apduString.length() > 8)
            APDU_HEAD_LENGTH = 5;
        else
            APDU_HEAD_LENGTH = 4;

        bytes = new int[apduString.length() / 2];           // "80304589..." -> {80h, 30h, 45h,89h, ...}
        if(apduString.length() > 8)                                                 // определение длины тела APDU-команды
            bodyLength = Integer.parseInt(apduString.substring(8, 10), 16);
        else
            bodyLength = 0;
        int i = 0;
        while (i < APDU_HEAD_LENGTH + bodyLength) {                                 // Преобразование APDU-команды строки в массив байтов
            try {
                bytes[i] = Integer.parseInt(apduString.substring(2 * i, 2 * i + 2), 16);
            } catch (NumberFormatException e) {
                bytes = null;
                e.printStackTrace();
                break;
            }catch (StringIndexOutOfBoundsException e){
                e.printStackTrace();
                break;
            }
            i++;
        }
        return bytes;
    }

    /*
        Создание APDU-команды как объекта класса javax.smartcardio.CommandAPDU;
     */
    static CommandAPDU create(String apduString) {
        int APDU_HEAD_LENGTH;                   // длина заглавия APDU-команды
        if(apduString.length() > 8)            // если длина APDU-команды как строки больше 8ми
            APDU_HEAD_LENGTH = 5;               // то длина заголовка APDU-команды - 5
        else                                    // иначе
            APDU_HEAD_LENGTH = 4;               // 4
        int[] bytes = getBytes(apduString);     // массив байтов APDU-команды
        int bodyLength;                         // длина тела APDU-команды
        int dataLength;                         // длина APDU-команды
        int cla, ins, p1, p2, len;              // заголовочные байты APDU-команды
        CommandAPDU returnAPDUCommand = null;   // javax.smartcardio.CommandAPDU;

        if(bytes != null) {                     // определение заголовочных байтов APDU-команды
            cla = bytes[0];
            ins = bytes[1];
            p1  = bytes[2];
            p2  = bytes[3];
            if(APDU_HEAD_LENGTH == 4)
                len = 0;
            else
                len = bytes[4];

            bodyLength = (len < 1) ? 0 : bytes.length - APDU_HEAD_LENGTH;   // определение длины тела APDU-команды
            dataLength = (bodyLength < len) ? bodyLength : len;             // определение длины APDU-команды
            byte[] data = new byte[dataLength];
            for(int i = 0; i < data.length; i++)                            // извлечение тела APDU-команды из строки APDU-команды
                data[i] = (byte) bytes[i + APDU_HEAD_LENGTH];
            returnAPDUCommand = new CommandAPDU(cla, ins, p1, p2, data);
        }
        return returnAPDUCommand;
    }

    /*
        Представление javax.smartcardio.CommandAPDU строкой
     */
    static String view(CommandAPDU apduCommand) {
        StringBuilder strBuilder = new StringBuilder("");
        int bodyStringsCount;                               // количество строк в отображении тела APDU-команды
        int APDU_HEAD_LENGTH;                               // длина заглавия APDU-команды
        int STRING_LENGTH = 16;                             // длина строки в отображении тела APDU-команды

        if(apduCommand.getNc() == 0)                        // определение длины тела APDU-команды
            APDU_HEAD_LENGTH = 4;
        else
            APDU_HEAD_LENGTH = 5;
        strBuilder.append("  Send command -->\n\t");
        byte[] bytes = apduCommand.getBytes();
        int bodyLength = bytes.length - APDU_HEAD_LENGTH;                                                                       // определение длины тела APDU-команды
        for (int i = 0; i < APDU_HEAD_LENGTH; i++)                                                                              // составление строки APDU-команды
            strBuilder.append(String.format("%02X ", bytes[i]));
        bodyStringsCount = (bodyLength % STRING_LENGTH == 0) ? bodyLength / STRING_LENGTH : (1 + bodyLength / STRING_LENGTH);   // определение количества строк тела APDU-команды
        for (int j = 0; j < bodyStringsCount; j++) {                                                                            // составление строки APDU-команды
            strBuilder.append("\n\t");
            for (int i = 0; i < ((bodyLength > STRING_LENGTH) ? STRING_LENGTH : bodyLength); i++)
                strBuilder.append(String.format("%02X ", bytes[APDU_HEAD_LENGTH + j * STRING_LENGTH + i]));
            bodyLength = bodyLength - STRING_LENGTH;
        }

        return strBuilder.toString();
    }
}
