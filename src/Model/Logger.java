package Model;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger implements Closeable{

    private FileWriter fw;

    public Logger(File log) throws IOException {
        fw = new FileWriter(log);
    }

    private void writeStreak() throws IOException {
        writeLine("_______________________________________________________\n\n");
    }

    private void writeLine(String line) throws IOException {
        fw.write("\n" + line);
        fw.flush();
    }

    public void writeHeader() throws IOException {
        writeStreak();
        writeLine(String.format("\t%s\n", Loader.getTerminal()));
        writeLine(String.format("\t%s\n", new Date()));
        writeStreak();
    }

    public void writeTail(int countError, int countFail) throws IOException {
        writeStreak();
        writeLine(String.format("\tERROR : %d\n", countError));
        writeLine(String.format("\tFAILURE : %d\n", countFail));
        writeStreak();
    }

    @Override
    public void close() throws IOException {
        fw.close();
    }
}
