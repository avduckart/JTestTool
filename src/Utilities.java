public class Utilities {
    public static String bytesToString(byte[] bytes){
        StringBuilder result = new StringBuilder();
        for (byte b : bytes)
            result.append(String.format("%02X", b));
        return result.toString();
    }


    public static byte[] stringToBytes(String line){
        int size = line.length() / 2;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++)
            bytes[i] = (byte)Integer.parseInt(line.substring(2 * i, 2 * i + 2), 16);
        return bytes;
    }
}
