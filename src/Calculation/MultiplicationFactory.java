package Calculation;

public class MultiplicationFactory implements OperationFactory{
    @Override
    public Operation create() {
        return new Multiplication();
    }
}
