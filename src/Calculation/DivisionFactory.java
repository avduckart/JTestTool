package Calculation;

public class DivisionFactory implements OperationFactory{
    @Override
    public Operation create() {
        return new Division();
    }
}
