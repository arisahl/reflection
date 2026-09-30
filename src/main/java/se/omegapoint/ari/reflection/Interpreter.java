package se.omegapoint.ari.reflection;

public class Interpreter {
    static void main(String[] args) {
        final String code = "java.lang.System.out.println(\"Hello World\");";

        new Interpreter().interpret(code);

    }

    private void interpret(String code) {
    }
}
