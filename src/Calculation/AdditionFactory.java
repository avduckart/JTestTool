package Calculation;

public class AdditionFactory implements OperationFactory{
    private static Addition addition = null;

    @Override
    public Operation create() {
        if(addition == null)
            addition = new Addition();
        return addition;
    }
}
