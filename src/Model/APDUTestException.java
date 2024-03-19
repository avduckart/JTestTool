package Model;

public class APDUTestException extends Throwable{

    public void printStackTrace(String command) {
        System.out.println(String.format("Ошибка в команде : %s\n", command));
    }
}
