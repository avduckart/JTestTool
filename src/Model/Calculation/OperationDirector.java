package Model.Calculation;

public class OperationDirector{
    public OperationFactory getFactory(char op) {
        switch (op) {
            case '+':
                return new AdditionFactory();
            case '-':
                return new SubstractionFactory();
            case '*':
                return new MultiplicationFactory();
            case '/':
                return new DivisionFactory();
            case '%':
                return new ModulationFactory();
            default:
                throw new ArithmeticException();
        }
    }
}
