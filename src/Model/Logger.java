package Model;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger implements Closeable{

    private File log;
    private FileWriter fw;

    public Logger(File log) throws IOException {
        this.log = log;
        fw = new FileWriter(log);
    }

    Logger(String logPath) throws IOException {
        this(new File(logPath));
    }

    public File getLog() {
        return log;
    }

    public void append(String line) throws IOException {
        fw.write("\n" + line);
        fw.flush();
    }

    void writeHeader() throws IOException {
        append("_______________________________________________________\n\n");
        append(String.format("\t%s\n", Loader.getTerminal().toString()));
        append(String.format("\t%s\n", new Date().toString()));
        append("_______________________________________________________\n\n");
    }

    void writeTail(int countError, int countFail) throws IOException {
        append("_______________________________________________________\n\n");
        append(String.format("\tERROR : %d\n", countError));
        append(String.format("\tFAILURE : %d\n", countFail));
        append("_______________________________________________________\n\n");
    }

    void recordResult(boolean result) throws IOException {
        if(result)
            append("  COMPLETE\n\n");
        else
            append("  FAILURE\n\n");
    }

    @Override
    public void close() throws IOException {
        fw.close();
    }
}
