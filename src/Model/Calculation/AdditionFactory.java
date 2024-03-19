package Model.Calculation;

public class AdditionFactory implements OperationFactory{
    @Override
    public Operation create() {
        return new Addition();
    }
}
