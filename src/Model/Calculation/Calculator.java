package Model.Calculation;

public class Calculator {

    public static String operator(char op, String a, String b){
        OperationDirector director = new OperationDirector();
        OperationFactory operationFactory = director.getFactory(op);
        Operation operation = operationFactory.create();

        return zeroPadding(a, b, operation.execute(a, b));
    }

    private static int getReturnLength(String a, String b, String c){
        return (a.length() >= b.length())        ?
                Math.max(a.length(), c.length()) :
                Math.max(b.length(), c.length());
    }

    private static String zeroPadding(String a, String b, String result) {
        int length = getReturnLength(a, b, result);

        StringBuilder resultBuilder = new StringBuilder(result);
        for (int count = Math.abs(result.length() - length); count > 0; count--)
            resultBuilder.insert(0, "0");

        return resultBuilder.toString();
    }
}
