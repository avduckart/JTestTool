import javax.smartcardio.*;
import java.io.*;
import java.util.*;

public class ScriptRunner {

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

    public static CardChannel getChannel(){
        return channel;
    }

    public static void main(String args[]) {

        if (args.length != 3)
            return;

        String command = args[0];
        String regime = args[1];
        String value = args[2];

        if (!"run".equals(command)) {
            System.out.println("Uncorrected command");
            return;
        }

        if ("-c".equals(regime) || "--command".equals(regime)) {
            CommandAPDU apduCommand = APDUCommand.create(value);
            ResponseAPDU apduResponse = channel.transmit(apduCommand);
            System.out.println(APDUResponse.toString(apduResponse));
            return;
        }

        ArrayList<String> files = new ArrayList<>();
        if ("-s".equals(regime) || "--script".equals(regime))
            files.add(value);
        else if ("-f".equals(regime) || "--folder".equals(regime)) {
            File scriptFolder = new File(value);
            files.addAll(Arrays.asList(Objects.requireNonNull(scriptFolder.list((folder, name) -> name.endsWith(".s")))));
        } else {
            return;
        }

        for (String scriptPath : files) {
            System.out.println(scriptPath.toUpperCase());
            if (regime.equals("-f"))
                scriptPath = value + "\\" + scriptPath;
            scriptExecute(scriptPath);
        }
    }

    private static void scriptExecute(String scriptPath) {
        ScriptScanner scriptScanner = new ScriptScanner(scriptPath);
        scriptScanner.scan();
    }

    private static CardTerminal selectTerminal() throws CardException {
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        CardTerminals terminals = terminalFactory.terminals();
        List<CardTerminal> terminalList = terminals.list();

        for (CardTerminal x : terminalList)
            System.out.printf("%x    %s\n", terminalList.indexOf(x) + 1, x);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int terminalNumber = Integer.parseInt(reader.readLine());

            if(terminalNumber < 1 || terminalNumber > terminalList.size())
                return null;

            return terminalList.get(terminalNumber - 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}