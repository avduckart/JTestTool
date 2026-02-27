package Calculation;

public class SubstractionFactory implements OperationFactory{
    private static Substraction substraction = null;

    @Override
    public Operation create() {
        if(substraction == null)
            substraction = new Substraction();
        return substraction;
    }
}
