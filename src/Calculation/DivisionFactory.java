package Calculation;

public class DivisionFactory implements OperationFactory{
    private static Division division = null;

    @Override
    public Operation create() {
        if(division == null)
            division = new Division();
        return division;
    }
}
