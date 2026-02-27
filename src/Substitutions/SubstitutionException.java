package Substitutions;

public class SubstitutionException extends Throwable{

    public void printStackTrace(String command) {
        System.out.println(String.format("Ошибка в команде : %s\n", command));
    }
}
