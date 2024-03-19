package Model.Calculation;

public class ModulationFactory implements OperationFactory{
    @Override
    public Operation create() {
        return new Modulation();
    }
}
