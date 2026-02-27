package Calculation;

public class ModulationFactory implements OperationFactory{
    private static Modulation modulation = null;

    @Override
    public Operation create() {
        if(modulation == null)
            modulation = new Modulation();
        return modulation;
    }
}
