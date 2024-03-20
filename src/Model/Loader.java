package Model;

import Model.ScriptScanner.ScriptScanner;
import com.sun.istack.internal.Nullable;
import org.bouncycastle.crypto.CryptoException;

import javax.smartcardio.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Loader {

    private static CardTerminal terminal;
    private static CardChannel channel;

    static {
        try {
            Card card;
            terminal = selectTerminal();
            if (terminal != null) {
                card = terminal.connect("*");
                channel = card.getBasicChannel();
            }
        } catch (CardException e) {
            e.printStackTrace();
        }
    }

    public static CardTerminal getTerminal() {
        return terminal;
    }

    public static CardChannel getChannel() {
        return channel;
    }

    public static void main(String args[]) throws CardException, IOException, CommandExecuteException, CryptoException {

        String command;
        String regime;
        String value;
        ResponseAPDU apduResponse;
        CommandAPDU apduCommand;
        Logger logger = null;

        /*
            command regime value [log]
         */
        if (args.length < 3) {
            throw new CommandExecuteException();
        } else {
            command = args[0];
            regime = args[1];
            value = args[2];

            if ("run".equals(command)) {
                if ("-c".equals(regime) || "--command".equals(regime)) {
                    if(args.length != 3)
                        throw new CommandExecuteException();
                    apduCommand = APDUCommand.create(value);
                    apduResponse = channel.transmit(apduCommand);
                    System.out.println(APDUResponse.toString(apduResponse));
                } else {
                    if (args.length > 4)
                        throw new CommandExecuteException();
                    ArrayList<String> files = new ArrayList<>();
                    if ("-s".equals(regime) || "--script".equals(regime))
                        files.add(value);
                    else if ("-f".equals(regime) || "--folder".equals(regime)) {
                        File scriptFolder = new File(value);
                        files.addAll(Arrays.asList(Objects.requireNonNull(scriptFolder.list((folder, name) -> name.endsWith(".s")))));
                    }
                    for (String scriptPath : files) {
                        System.out.println(scriptPath.toUpperCase());
                        File log = new File(String.format("%s %s",
                                new SimpleDateFormat("MM-dd-yyyy hh-mm-ss").format(new Date()),
                                "QWER" /* scriptPath.substring(scriptPath.lastIndexOf('\\') + 1))*/));
                        logger = new Logger(log);
                        if(regime.equals("-f"))
                            scriptPath = value + "\\" + scriptPath;
                        scriptExecute(scriptPath, logger);
                    }
                }
            } else
                System.out.println("Uncorrected command");
            if (logger != null)
                logger.close();
        }
    }

    public static void scriptExecute(String scriptPath, Logger logger) throws CardException, IOException, CryptoException {
        ScriptScanner scriptScanner = new ScriptScanner(scriptPath);
        if(logger != null)
            logger.writeHeader();
        scriptScanner.scanAndExecuteTest(logger);
        if(logger != null)
            logger.writeTail(scriptScanner.getErrorCount(), scriptScanner.getFailCount());
    }

    @Nullable
    private static CardTerminal selectTerminal() throws CardException {
        int terminalNumber;
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        CardTerminals terminals = terminalFactory.terminals();
        List<CardTerminal> terminalList = terminals.list();
        for (CardTerminal x : terminalList)
            System.out.printf("%x    %s\n", terminalList.indexOf(x) + 1, x);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            terminalNumber = Integer.parseInt(reader.readLine());
            return terminalList.get(terminalNumber - 1);
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}