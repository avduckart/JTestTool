package Model.ScriptScanner;

import Model.*;

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
    private String receivedResponse = "";
    private String expectedResponse = "";

    private String testRegExp = "^[\\dA-F()]+,+([\\dA-F]|(\\(\\d+(,\\d+)?\\)L))+$";
    Pattern testPattern = Pattern.compile(testRegExp);
    private Matcher testMatcher;// = testPattern.matcher(null/*s*/);
    private int failCount = 0;
    private int errorCount = 0;

    private final Substitution[] substitutions = {
            DeclarationSubstitution.getInstance(), ValueSubstitution.getInstance(), VariableSubstitution.getInstance(),
            MathOperationSubstitution.getInstance(), HashSubstitution.getInstance(), HmacSubstitution.getInstance(),
            Pbkdf2Substitution.getInstance(), CfbSubstitution.getInstance() , EcbSubstitution.getInstance(), RandSubstitution.getInstance(),
            SubstringSubstitution.getInstance(), InvertSubstitution.getInstance(), MultiplePointSubstitution.getInstance(),
            SumPointSubstitution.getInstance(), DiversifySubstitution.getInstance()
    };

    private ScriptScanner(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public ScriptScanner(String scriptPath) {
        this(new File(scriptPath));
    }

    /*
        Выполнение сценария .s
     */
    public void scanAndExecuteTest(Logger logger) throws IOException, CardException {
        String info = "^>.*";
        String comment = "^#.*";
        Scanner scanner = new Scanner(scriptFile);
        String currentLine;
        StringBuilder commandSequence = new StringBuilder();
        int count = 0;

        currentLine = scanner.nextLine();
        while (scanner.hasNextLine()) {
            if(currentLine.matches(info)) {
                reflectToLog(currentLine, logger);
                while (scanner.hasNextLine() && (!(currentLine = scanner.nextLine()).matches(info))) {
                    count++;
                    if(!currentLine.matches(comment))
                        commandSequence.append(currentLine);
                }
            }
            try {
                executeTestSequence(commandSequence.toString(), logger);
                commandSequence.delete(0, commandSequence.length());
            }
            catch (APDUTestException e){
                reflectToLog(String.format("Uncorrected Test in string with number %d\n", count), logger);
                break;
            }
        }
        scanner.close();
    }

    private void executeTestSequence(String commandTestSequence, Logger logger) throws IOException, CardException, APDUTestException {
        String[] lines = commandTestSequence.replaceAll("\\s+","").split(";+");
        for (String line : lines)
            processLine(logger, line);
    }

    private void processLine(Logger logger, String line) throws APDUTestException, CardException, IOException {

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
            executeTest(logger, line);
        }
    }

    private void executeTest(Logger logger, String line) throws APDUTestException, CardException, IOException {
        System.out.println("  № " + ++counter);
        runAndCompare(line, logger);
        expectedResponse = getExpectedResponseFromTest(line);
    }

    private String getExpectedResponseFromTest(String line) throws APDUTestException {
        String[] strArr;
        if((strArr = line.split(",+",2)).length != 2)
            throw new APDUTestException();
        return strArr[1];
    }

    private void runAndCompare(String apduPair, Logger logger) throws CardException, IOException {
        String[] commandAndResult;
        String command;
        String expectedResponse;
        ResponseAPDU apduResponse;
        boolean result;

        commandAndResult = apduPair.split(",+", 2);
        try {
            if(commandAndResult.length != 2)
                throw new APDUTestException();
            command = commandAndResult[0];
            expectedResponse = commandAndResult[1];
            expectedResponse = replaceExpectedLengthToRegExp(expectedResponse);
            apduResponse = runCommandAndReflect(logger, command);
            receivedResponse = APDUResponse.toString(apduResponse);
            result = compareToExpectedResult(expectedResponse, receivedResponse, logger);
            if(!result)
                failCount++;
        } catch (APDUTestException e) {
            e.printStackTrace(apduPair);
            errorCount += 1;
        }
    }

    private ResponseAPDU runCommandAndReflect(Logger logger, String command) throws IOException, CardException {
        CommandAPDU apduCommand = APDUCommand.create(command);
        reflectToLog(APDUCommand.view(apduCommand), logger);
        ResponseAPDU apduResponse = Loader.getChannel().transmit(apduCommand);
        reflectToLog(APDUResponse.view(apduResponse), logger);
        return apduResponse;
    }

    private boolean compareToExpectedResult(String expectedResponse, String gettingResult, Logger logger) throws IOException {
        boolean result = gettingResult.matches(expectedResponse);
        reflectResult(result, logger);
        return result;
    }

    private String extractBtwBrackets(String s){
        int firstInd = s.indexOf('(');
        int secondInd = firstInd + s.substring(firstInd).indexOf(')');
        return  s.substring(firstInd + 1, secondInd);
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

    private void reflectToLog(String line, Logger logger) throws IOException {
        reflect(line);
        if(logger != null)
            logger.append(line);
    }

    private void reflect(String line){
        System.out.println(line);
    }

    private void reflectResult(boolean bool, Logger logger) throws IOException {
        if(bool)
            reflectToLog("  COMPLETE\n\n", logger);
        else
            reflectToLog("  FAILURE\n\n", logger);
    }

    public int getErrorCount() {
        return 0;
    }

    public int getFailCount() {
        return 0;
    }
}
