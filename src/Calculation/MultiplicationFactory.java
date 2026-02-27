package Calculation;

public class MultiplicationFactory implements OperationFactory{
    private static Multiplication multiplication = null;

    @Override
    public Operation create() {
        if(multiplication == null)
            multiplication = new Multiplication();
        return multiplication;
    }
}
