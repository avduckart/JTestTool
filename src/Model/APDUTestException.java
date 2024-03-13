package Model;

class APDUTestException extends Throwable{

    void printStackTrace(String command) {
        System.out.println(String.format("Ошибка в команде : %s\n", command));
    }
}
