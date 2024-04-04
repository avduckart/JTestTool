package Model.ScriptScanner;

import Model.*;
import Model.ScriptScanner.Substitutions.*;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptScanner {

    static private int counter = 0;

    private File scriptFile;

    private String testRegExp = "^[\\dA-F()]+,+([\\dA-F]|(\\(\\d+(,\\d+)?\\)L))+$";
    Pattern testPattern = Pattern.compile(testRegExp);
    private Matcher testMatcher;// = testPattern.matcher(null/*s*/);
    private int failCount = 0;
    private int errorCount = 0;

    private final Substitution[] substitutions = {
            new DeclarationSubstitution(), new ValueSubstitution(), new VariableSubstitution(),
            new MathOperationSubstitution(), new HashSubstitution(), new HmacSubstitution(),
            new Pbkdf2Substitution(), new CfbSubstitution() , new EcbSubstitution(), new RandSubstitution(),
            new SubstringSubstitution(), new InvertSubstitution(), new MultiplePointSubstitution(),
            new SumPointSubstitution(), new DiversifySubstitution()
    };

    private ScriptScanner(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public ScriptScanner(String scriptPath) {
        this(new File(scriptPath));
    }

    public void scan() throws IOException, CardException {
        String info = "^>.*";
        String comment = "^#.*";
        Scanner scanner = new Scanner(scriptFile);
        int num = 0;

        for(String currentLine = scanner.nextLine(); scanner.hasNextLine(); currentLine = scanner.nextLine()) {
            num++;

            if(currentLine.matches(comment))
                continue;

            if(currentLine.matches(info)) {
                outputToConsole(currentLine);
                continue;
            }

            try {
                String[] commands = currentLine.replaceAll("\\s+","").split(";+");
                for (String command : commands)
                    processLine(command);
            }
            catch (APDUTestException e){
                outputToConsole(String.format("Uncorrected Test in string with number %d\n", num));
                break;
            }
        }
        scanner.close();
    }

    private void processLine(String line) throws APDUTestException {

        int match;
        do {
            match = 0;
            for (Substitution subst : substitutions) {
                match += (subst.isFound()) ? 1 : 0;
                subst.reset(line);
                line = subst.replace(line);
            }
        }while(match != 0);

        testMatcher = testPattern.matcher(line);
        if(testMatcher.matches()) {
            testMatcher.reset();
            testRun(line);
        }
    }

    private void testRun(String apduPair) {
        outputToConsole("  № " + ++counter);

        String[] commandAndResult = apduPair.split(",+", 2);

        if(commandAndResult.length != 2) {
            errorCount++;
            return;
        }

        String command = commandAndResult[0];
        String expectedResponse = commandAndResult[1];
        expectedResponse = replaceExpectedLengthToRegExp(expectedResponse);
        ResponseAPDU apduResponse = commandExecute(command);
        String receivedResponse = APDUResponse.toString(apduResponse);
        if(!compare(expectedResponse, receivedResponse))
            failCount++;
    }

    private ResponseAPDU commandExecute(String command) {
        try {
            CommandAPDU apduCommand = APDUCommand.create(command);
            outputToConsole(APDUCommand.view(apduCommand));
            ResponseAPDU apduResponse = Loader.getChannel().transmit(apduCommand);
            outputToConsole(APDUResponse.view(apduResponse));
            return apduResponse;
        } catch (CardException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean compare(String expectedResponse, String response) {
        boolean result = response.matches(expectedResponse);
        outputToConsole(result ? "  COMPLETE\n\n" : "  FAILURE\n\n");
        return result;
    }

    private String extractBtwBrackets(String s){
        int firstInd = s.indexOf('(');
        int secondInd = firstInd + s.substring(firstInd).indexOf(')');
        return s.substring(firstInd + 1, secondInd);
    }

    private String replaceExpectedLengthToRegExp(String expectedResponse) {
        String[] interval;
        String responseWithExpectedLength = ".*\\(\\d+(,\\d+)?\\)L.*";
        while(expectedResponse.matches(responseWithExpectedLength)) {
            String lengthL = extractBtwBrackets(expectedResponse);
            if(lengthL.contains(",")) {
                interval = lengthL.split(",+", 2);
                expectedResponse = expectedResponse.replaceFirst("\\(\\d+(,\\d+)?\\)L", "[\\\\dA-F]{" + 2 * Integer.parseInt(interval[0]) + "," + 2 * Integer.parseInt(interval[1]) + "}");
            }else
                expectedResponse = expectedResponse.replaceFirst("\\(\\d+(,\\d+)?\\)L", "[\\\\dA-F]{" + 2 * Integer.parseInt(lengthL) + "}");
        }
        return expectedResponse;
    }

    private void outputToConsole(String line){
        System.out.println(line);
    }
}
