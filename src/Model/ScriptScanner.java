package Model;

import Model.JTestCrypto.ECPointOperation;
import Model.JTestCrypto.EncryptionGOST_TC26;
import Model.JTestCrypto.HMAC;
import Model.JTestCrypto.JTestDigest.JTestDigest;
import Model.JTestCrypto.PBKDF2;
import org.bouncycastle.crypto.CryptoException;

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

    private int errorCount = 0;
    private int failCount = 0;
    private File scriptFile;
    private static HashMap<String, String> variablesMap = new HashMap<>();
    private String receivedResponse = "";
    private String expectedResponse = "";

    private String testRegExp = "^[\\dA-F()]+,+([\\dA-F]|(\\(\\d+(,\\d+)?\\)L))+$";
    private String declarationRegExp = "^(%\\w+%)=";
    private String valueRegExp = "value\\(\\d+\\)";
    private String denialRegExp = "!\\([\\dA-F,]+\\)";
    private String mathOperationRegExp = "\\([\\dA-F]+[+*\\-/%][\\dA-F]+\\)";
    private String hashRegExp = "hash(094|256|512)\\([\\dA-F]+\\)";
    private String hmacRegExp = "hmac(094|256|512)\\([\\dA-F]+,[\\dA-F]{64}\\)";
    private String pbkdf2RegExp = "pbkdf2\\([\\dA-F]+,[\\dA-F]+,\\d+,\\d+\\)";
    private String ecfbRegExp = "ecfb\\([\\dA-F]+,[\\dA-F]{16},[\\dA-F]{64}\\)";
    private String dcfbRegExp = "dcfb\\([\\dA-F]+,[\\dA-F]{16},[\\dA-F]{64}\\)";
    private String eecbRegExp = "eecb\\([\\dA-F]+,[\\dA-F]{64}\\)";
    private String decbRegExp = "decb\\([\\dA-F]+,[\\dA-F]{64}\\)";
    private String imitRegExp = "imit\\([\\dA-F]+,[\\dA-F]{64},\\d+\\)";
    private String mulpointRegExp = "mulpoint\\([\\dA-F]{128},[\\dA-F]+\\)";
    private String addpointRegExp = "addpoint\\([\\dA-F]{128},[\\dA-F]{128}\\)";
    private String diverseRegExp = "diverse\\([\\dA-F]+,[\\dA-F]{64},[\\dA-F]{128}\\)";
    private String substrRegExp = "substr\\([\\dA-F]+,\\d+,\\d+\\)";
    private String invertRegExp = "invert\\([\\dA-F]+\\)";
    private String randRegExp = "rand\\(\\d+\\)";
    private String variableRegExp = "%\\w+%";

    private Pattern test = Pattern.compile(testRegExp);
    private Pattern declaration = Pattern.compile(declarationRegExp);
    private Pattern value = Pattern.compile(valueRegExp);
    private Pattern denial = Pattern.compile(denialRegExp);
    private Pattern mathOperation = Pattern.compile(mathOperationRegExp);
    private Pattern hash = Pattern.compile(hashRegExp);
    private Pattern hmac = Pattern.compile(hmacRegExp);
    private Pattern pbkdf2 = Pattern.compile(pbkdf2RegExp);
    private Pattern ecfb = Pattern.compile(ecfbRegExp);
    private Pattern dcfb = Pattern.compile(dcfbRegExp);
    private Pattern eecb = Pattern.compile(eecbRegExp);
    private Pattern decb = Pattern.compile(decbRegExp);
    private Pattern imit = Pattern.compile(imitRegExp);
    private Pattern mulpoint = Pattern.compile(mulpointRegExp);
    private Pattern addpoint = Pattern.compile(addpointRegExp);
    private Pattern diverse = Pattern.compile(diverseRegExp);
    private Pattern variable = Pattern.compile(variableRegExp);
    private Pattern substr = Pattern.compile(substrRegExp);
    private Pattern invert = Pattern.compile(invertRegExp);
    private Pattern rand = Pattern.compile(randRegExp);

    private Matcher testMatcher;
    private Matcher declarationMatcher;
    private Matcher valueMatcher;
    private Matcher mathOperationMatcher;
    private Matcher variableMatcher;
    private Matcher denialMatcher;
    private Matcher hashMatcher;
    private Matcher hmacMatcher;
    private Matcher pbkdf2Matcher;
    private Matcher ecfbMatcher;
    private Matcher dcfbMatcher;
    private Matcher eecbMatcher;
    private Matcher decbMatcher;
    private Matcher imitMatcher;
    private Matcher mulpointMatcher;
    private Matcher addpointMatcher;
    private Matcher substrMatcher;
    private Matcher invertMatcher;
    private Matcher randMatcher;
    private Matcher diverseMatcher;

    private ScriptScanner(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    ScriptScanner(String scriptPath) {
        this(new File(scriptPath));
    }

    int getFailCount() {
        return failCount;
    }

    int getErrorCount() {
        return errorCount;
    }

    /*
        Выполнение сценария .s
     */
    void scanAndExecuteTest(Logger logger) throws IOException, CardException, CryptoException {
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

    private void executeTestSequence(String commandTestSequence, Logger logger) throws IOException, CardException, APDUTestException, CryptoException {
        String[] lines = commandTestSequence.replaceAll("\\s+","").split(";+");
        for (String line : lines)
            processLine(logger, line);
    }

    private void processLine(Logger logger, String line) throws APDUTestException, CardException, IOException, CryptoException {
        initAllMatchers(line);
        while(true) {
            initAllMatchers(line);
            resetAllMatchers();
            if (declarationMatcher.find()) {
                addToVariablesMap(line);
                break;
            }
            if (valueMatcher.find()) {
                line = replaceAllExpressionToValue(line);
                continue;
            }
            if (variableMatcher.find()) {
                line = replaceVariablesToValue(line);
                continue;
            }
            if (mathOperationMatcher.find()) {
                line = executeAllMathOperation(line);
                continue;
            }
            if(denialMatcher.find()){
                //line =
                //continue;
            }
            if(hashMatcher.find()){
                line = replaceHash(line);
                continue;
            }
            if(hmacMatcher.find()){
                line = replaceHmac(line);
                continue;
            }
            if(pbkdf2Matcher.find()){
                line = replacePbkdf2(line);
                continue;
            }
            if (ecfbMatcher.find()){
                line = replaceECFB(line);
                continue;
            }
            if (dcfbMatcher.find()){
                line = replaceDCFB(line);
                continue;
            }
            if (eecbMatcher.find()){
                line = replaceEECB(line);
                continue;
            }
            if (decbMatcher.find()){
                line = replaceDECB(line);
                continue;
            }
            if (imitMatcher.find()){
                line = replaceImit(line);
                continue;
            }
            if (randMatcher.find()){
                line = replaceRand(line);
                continue;
            }
            if (substrMatcher.find()){
                line = replaceSubstr(line);
                continue;
            }
            if (invertMatcher.find()){
                line = replaceInvert(line);
                continue;
            }
            if (mulpointMatcher.find()){
                line = replaceMulpoint(line);
                continue;
            }
            if (addpointMatcher.find()){
                line = replaceAddpoint(line);
                continue;
            }
            if (diverseMatcher.find()){
                line = replaceDiverse(line);
                continue;
            }
            else
                break;
        }
        if(testMatcher.matches()) {
            testMatcher.reset();
            executeTest(logger, line);
        }
    }

    private String replaceImit(String line) throws CryptoException {
        initImitMatcher(line);
        String text;
        while (imitMatcher.find()){
            text = imitMatcher.group();
            line = line.replaceFirst(imitRegExp, imitGost(text));
            initImitMatcher(line);
        }
        return line;
    }

    private String replacePbkdf2(String line) {
        initPbkdf2Matcher(line);
        String text;
        while (pbkdf2Matcher.find()){
            text = pbkdf2Matcher.group();
            line = line.replaceFirst(pbkdf2RegExp, calculatePbkdf2(text));
            initPbkdf2Matcher(line);
        }
        return line;
    }

    private String calculatePbkdf2(String line) {
        pbkdf2Matcher.reset();
        String[] pbkdfArgs = extractBtwBrackets(line).split(",+");
        String password = pbkdfArgs[0];
        String salt = pbkdfArgs[1];
        String count = pbkdfArgs[2];
        String dkLen = pbkdfArgs[3];
        return PBKDF2.pbkdf2(password, salt, count, dkLen);
    }

    private String replaceAddpoint(String line) {
        initAddPoinMather(line);
        String text;
        while (addpointMatcher.find()){
            text = addpointMatcher.group();
            line = line.replaceFirst(addpointRegExp, calcAddPoint(text));
            initAddPoinMather(line);
        }
        return line;
    }

    private String calcAddPoint(String text) {
        addpointMatcher.reset();
        String[] pointAndPoint = extractBtwBrackets(text).split(",+");
        String point1 = pointAndPoint[0];
        String point2 = pointAndPoint[1];
        return ECPointOperation.sumPoint(point1, point2);
    }

    private String replaceDiverse(String line) {
        initDiverseMatcher(line);
        String text;
        while (diverseMatcher.find()){
            text = diverseMatcher.group();
            line = line.replaceFirst(diverseRegExp, diverseKey(text));
            initDiverseMatcher(line);
        }
        return line;
    }

    private String replaceRand(String line) {
        initRandMatcher(line);
        String text;
        while (randMatcher.find()){
            text = randMatcher.group();
            line = line.replaceFirst(randRegExp, getRandom(text));
            initRandMatcher(line);
        }
        return line;
    }

    private String replaceSubstr(String line) {
        initSubstrMatcher(line);
        String[] text;
        int start, end;
        while (substrMatcher.find()){
            text = extractBtwBrackets(substrMatcher.group()).split(",+");
            start = Integer.parseInt(text[1]);
            end = Integer.parseInt(text[2]);
            line = line.replaceFirst(substrRegExp, getSubstr(text[0], start, end));
            initSubstrMatcher(line);
        }
        return line;
    }

    private String replaceInvert(String line) {
        initInvertMatcher(line);
        String text;
        while (invertMatcher.find()){
            text = extractBtwBrackets(invertMatcher.group());
            line = line.replaceFirst(invertRegExp, invertByteOrder(text));
            initInvertMatcher(line);
        }
        return line;
    }

    private String replaceMulpoint(String line) {
        initMulPoinMather(line);
        String text;
        while (mulpointMatcher.find()){
            text = mulpointMatcher.group();
            line = line.replaceFirst(mulpointRegExp, calcMulPoint(text));
            initMulPoinMather(line);
        }
        return line;
    }

    private String replaceHmac(String line){
        initHmacMatcher(line);
        String text;
        while (hmacMatcher.find()){
            text = hmacMatcher.group();
            line = line.replaceFirst(hmacRegExp, calculateHmac(text));
            initHmacMatcher(line);
        }
        return line;
    }

    private String replaceEECB(String line) throws CryptoException {
        initEECBMatcher(line);
        String text;
        while(eecbMatcher.find()) {
            text = eecbMatcher.group();
            line = line.replaceFirst(eecbRegExp, encryptECB(text));
            initEECBMatcher(line);
        }
        return line;
    }

    private String replaceDECB(String line) throws CryptoException {
        initDECBMatcher(line);
        String text;
        while(decbMatcher.find()) {
            text = decbMatcher.group();
            line = line.replaceFirst(decbRegExp, decryptECB(text));
            initDECBMatcher(line);
        }
        return line;
    }

    private String replaceECFB(String line) throws CryptoException {
        initECFBMatcher(line);
        String text;
        while(ecfbMatcher.find()) {
            text = ecfbMatcher.group();
            line = line.replaceFirst(ecfbRegExp, encryptCFB(text));
            initECFBMatcher(line);
        }
        return line;
    }

    private String replaceDCFB(String line) throws CryptoException {
        initDCFBMatcher(line);
        String text;
        while(dcfbMatcher.find()) {
            text = dcfbMatcher.group();
            line = line.replaceFirst(dcfbRegExp, decryptCFB(text));
            initDCFBMatcher(line);
        }
        return line;
    }

    private String replaceHash(String line){
        initHashMatcher(line);
        String text;
        while(hashMatcher.find()) {
            text = hashMatcher.group();
            line = line.replaceFirst(hashRegExp, calculateHash(text));
            initHashMatcher(line);
        }
        return line;
    }

    private String encryptCFB(String message) throws CryptoException {
        ecfbMatcher.reset();
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        String text = textAndKey[0];
        String iv = textAndKey[1];
        String key = textAndKey[2];
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        return cipher.ecfb(text, iv, key);
    }

    private String decryptCFB(String message) throws CryptoException {
        dcfbMatcher.reset();
        String[] encAndKey = extractBtwBrackets(message).split(",+");
        String enc = encAndKey[0];
        String iv = encAndKey[1];
        String key = encAndKey[2];
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        return cipher.dcfb(enc, iv, key);
    }

    private String encryptECB(String message) throws CryptoException {
        eecbMatcher.reset();
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        String text = textAndKey[0];
        String key = textAndKey[1];
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        return XToY.bytesToString(cipher.eecb(XToY.stringToBytes(text), XToY.stringToBytes(key)));
    }

    private String decryptECB(String message) throws CryptoException {
        eecbMatcher.reset();
        String[] textAndKey = extractBtwBrackets(message).split(",+");
        String text = textAndKey[0];
        String key = textAndKey[1];
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        return XToY.bytesToString(cipher.decb(XToY.stringToBytes(text), XToY.stringToBytes(key)));
    }


    private String imitGost(String text) throws CryptoException {
        imitMatcher.reset();
        String[] messageAndKey = extractBtwBrackets(text).split(",+");
        String message = messageAndKey[0];
        String key = messageAndKey[1];
        int len = Integer.parseInt(messageAndKey[2]);
        EncryptionGOST_TC26 cipher = new EncryptionGOST_TC26();
        return XToY.bytesToString(cipher.imit(XToY.stringToBytes(message), XToY.stringToBytes(key), len));
    }

    private String getRandom(String text) {
        randMatcher.reset();
        int byteLength = Integer.parseInt(text);
        return String.format("%02X", (int)(Math.random()*256*byteLength));
    }

    private String invertByteOrder(String text) {
        invertMatcher.reset();
        byte[] bytes = XToY.stringToBytes(text);
        int length = bytes.length;
        byte tmp;
        for (int i = 0; i < length/2; i++){
            tmp = bytes[i];
            bytes[i] = bytes[length - 1 - i];
            bytes[length - 1 - i] = tmp;
        }
        return XToY.bytesToString(bytes);
    }

    private String getSubstr(String text, int start, int end) {
        substrMatcher.reset();
        return text.substring(2*start, 2*end);
    }

    private String calculateHash(String line){
        hashMatcher.reset();
        String hashAlg = line.substring(0,7);
        String m = extractBtwBrackets(line);
        JTestDigest hash = new JTestDigest(hashAlg);
        return hash.execute(m);
    }

    private String calculateHmac(String line){
        hmacMatcher.reset();
        String hmacAlg = line.substring(0,7);
        String[] hmacArgs = extractBtwBrackets(line).split(",+");
        String text = hmacArgs[0];
        String key = hmacArgs[1];
        HMAC hmac = new HMAC(hmacAlg);
        return hmac.hmac(text, key);
    }

    private String calcMulPoint(String text) {
        mulpointMatcher.reset();
        String[] pointAndMult = extractBtwBrackets(text).split(",+");
        String point = pointAndMult[0];
        String multiplier = pointAndMult[1];
        return ECPointOperation.getMultipledPoint(point, multiplier);
    }

    private String diverseKey(String text) {
        diverseMatcher.reset();
        String[] args = extractBtwBrackets(text).split(",+");
        String ukm = args[0];
        String ck = args[1];
        String ok = args[2];
        return ECPointOperation.diverseKey(ukm, ck, ok);
    }

    private void executeTest(Logger logger, String line) throws APDUTestException, CardException, IOException {
        System.out.println("  № " + ++counter);
        runAndCompare(line, logger);
        expectedResponse = getExpectedResponseFromTest(line);
    }

    private String executeAllMathOperation(String line) throws APDUTestException {
        mathOperationMatcher.reset();
        while(mathOperationMatcher.find()) {
            line = executeMathExpression(line);
            initMathOperationMatcher(line);
        }
        return line;
    }

    private String executeMathExpression(String line) throws APDUTestException {
        String findExpr = mathOperationMatcher.group();
        String sign = extractSign(findExpr);
        String arg1 = findExpr.split("[+\\-/*%]")[0].replaceFirst("\\(","");
        String arg2 = findExpr.split("[+\\-/*%]")[1].replaceFirst("\\)$","");
        String result = calculate(arg1, arg2, sign);
        line = line.replace(findExpr, result);
        return line;
    }

    private String calculate(String arg1, String arg2, String sign) throws APDUTestException {
        String result;
        switch (sign) {
            case "+":
                result = Calculator.add(arg1, arg2);
                break;
            case "-":
                result = Calculator.sub(arg1, arg2);
                break;
            case "/":
                result = Calculator.del(arg1, arg2);
                break;
            case "%":
                result = Calculator.mod(arg1, arg2);
                break;
            case "*":
                result = Calculator.mul(arg1, arg2);
                break;
            default:
                throw new APDUTestException();
        }
        return result;
    }

    private void initAllMatchers(String s){
        initDeclarationMatcher(s);
        initMathOperationMatcher(s);
        initTestMatcher(s);
        initValueMatcher(s);
        initVariableMatcher(s);
        initDenialMatcher(s);
        initHashMatcher(s);
        initHmacMatcher(s);
        initPbkdf2Matcher(s);
        initECFBMatcher(s);
        initEECBMatcher(s);
        initDECBMatcher(s);
        initImitMatcher(s);
        initMulPoinMather(s);
        initAddPoinMather(s);
        initInvertMatcher(s);
        initRandMatcher(s);
        initSubstrMatcher(s);
        initDCFBMatcher(s);
        initDiverseMatcher(s);
    }

    private void initDenialMatcher(String s) {
        denialMatcher = denial.matcher(s);
    }

    private void initTestMatcher(String s) {
        testMatcher = test.matcher(s);
    }

    private void initDeclarationMatcher(String s) {
        declarationMatcher = declaration.matcher(s);
    }

    private void initValueMatcher(String s) {
        valueMatcher = value.matcher(s);
    }

    private void initMathOperationMatcher(String s) {
        mathOperationMatcher = mathOperation.matcher(s);
    }

    private void initVariableMatcher(String s) {
        variableMatcher = variable.matcher(s);
    }

    private void initHashMatcher(String s){
        hashMatcher = hash.matcher(s);
    }

    private void initECFBMatcher(String s){
        ecfbMatcher = ecfb.matcher(s);
    }

    private void initEECBMatcher(String s){
        eecbMatcher = eecb.matcher(s);
    }

    private void initDECBMatcher(String s){
        decbMatcher = decb.matcher(s);
    }

    private void initHmacMatcher(String s){
        hmacMatcher = hmac.matcher(s);
    }

    private void initPbkdf2Matcher(String s){
        pbkdf2Matcher = pbkdf2.matcher(s);
    }

    private void initAddPoinMather(String s) {
        addpointMatcher = addpoint.matcher(s);
    }

    private void initMulPoinMather(String s) {
        mulpointMatcher = mulpoint.matcher(s);
    }

    private void initSubstrMatcher(String s) {
        substrMatcher = substr.matcher(s);
    }

    private void initInvertMatcher(String s) {
        invertMatcher = invert.matcher(s);
    }

    private void initRandMatcher(String s) {
        randMatcher = rand.matcher(s);
    }

    private void initDCFBMatcher(String s) {
        dcfbMatcher = dcfb.matcher(s);
    }

    private void initDiverseMatcher(String s) {
        diverseMatcher = diverse.matcher(s);
    }

    private void initImitMatcher(String s){
        imitMatcher = imit.matcher(s);
    }

    private void resetAllMatchers(){
        testMatcher.reset();
        declarationMatcher.reset();
        valueMatcher.reset();
        mathOperationMatcher.reset();
        variableMatcher.reset();
        denialMatcher.reset();
        hashMatcher.reset();
        hmacMatcher.reset();
        pbkdf2Matcher.reset();
        ecfbMatcher.reset();
        eecbMatcher.reset();
        decbMatcher.reset();
        imitMatcher.reset();
        substrMatcher.reset();
        invertMatcher.reset();
        mulpointMatcher.reset();
        addpointMatcher.reset();
        randMatcher.reset();
        diverseMatcher.reset();
        dcfbMatcher.reset();
    }

    private String extractSign(String s) throws APDUTestException {
        String sign = "[+\\-/*%]";
        Pattern pattern = Pattern.compile(sign);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find())
            return matcher.group();
        else
            throw new APDUTestException();
    }

    private String getExpectedResponseFromTest(String line) throws APDUTestException {
        String[] strArr;
        if((strArr = line.split(",+",2)).length != 2)
            throw new APDUTestException();
        return strArr[1];
    }

    private String replaceAllExpressionToValue(String line) throws APDUTestException {
        initValueMatcher(line);
        if (!receivedResponse.isEmpty()) {
            String value;
            while(valueMatcher.find()) {
                value = valueMatcher.group();
                line = line.replaceFirst("value\\(\\d+\\)", extractValueFromResponse(Integer.parseInt(extractBtwBrackets(value))));
                initValueMatcher(line);
            }
        }
        else
            throw new APDUTestException();
        return line;
    }

    private String extractSequenceFromResponse(int fromByte, int toByte){
        return receivedResponse.substring(fromByte, toByte);
    }

    private String extractValueFromResponse(int num){
        String length;
        String substr;
        int pos = 0;
        int addLen = 0;
        for(int i = 1; i < num; i++){
            length = extractBtwBrackets(expectedResponse.substring(pos));
            addLen += 2*Integer.parseInt(length);
            pos += expectedResponse.indexOf('L') + 1;
        }
        substr = expectedResponse.substring(pos);
        int from = pos + substr.indexOf('(') + addLen - 4*(num-1);
        length = extractBtwBrackets(substr);
        return extractSequenceFromResponse(from, from + 2*Integer.parseInt(length));
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
            errorCount++;
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

    private ArrayList<String> extractVariables(String line) {
        Pattern varDecl = Pattern.compile("%\\w+%");
        Matcher varDeclMatcher = varDecl.matcher(line);
        ArrayList<String> varList = new ArrayList<>();
        while(varDeclMatcher.find())
            varList.add(varDeclMatcher.group());
        return varList;
    }

    private String extractBtwBrackets(String s){
        int firstInd = s.indexOf('(');
        int secondInd = firstInd + s.substring(firstInd).indexOf(')');
        return  s.substring(firstInd + 1, secondInd);
    }

    private String replaceVariablesToValue(String line) {
        ArrayList<String> varList = extractVariables(line);
        for (String x : varList)
            line = line.replace(x, variablesMap.get(x));
        return line;
    }

    private void addToVariablesMap(String s) throws APDUTestException {
        String key = s.substring(s.indexOf('%'), s.indexOf('='));
        String value = s.substring(s.indexOf('=') + 1);
        initValueMatcher(value);
        if(valueMatcher.find())
            value = replaceAllExpressionToValue(value);
        variablesMap.put(key, value);
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
}
