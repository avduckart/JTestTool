package Model.Calculation;

public class SubstractionFactory implements OperationFactory{
    @Override
    public Operation create() {
        return new Substraction();
    }
}
