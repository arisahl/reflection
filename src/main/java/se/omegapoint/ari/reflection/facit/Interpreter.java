package se.omegapoint.ari.reflection.facit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Interpreter {
    static void main(String[] args) {
        final String code = "java.lang.System.out.println(\"Hello World\");";

        new Interpreter().interpret(code);

    }

    private void interpret(String code) {
        String[] tokens = tokenize(code, "\\.|;");
        if (tokens.length == 0) {
            throw new IllegalArgumentException("No code to interpret");
        }
        start(tokens);
    }

    private String[] tokenize(String candidate, String regex) {
        return candidate.split(regex);
    }

    private void start(String[] tokens) {
        char c = tokens[0].charAt(0);
        if (Character.isLowerCase(c)) {
            pack(tokens, 0, 0);
        } else if (Character.isUpperCase(c)) {
            clazz(tokens, 0, 0);
        } else {
            throw new IllegalArgumentException("Invalid token: " + tokens[0]);
        }
    }

    private void pack(String[] tokens, int startIndex, int currentIndex) {
        //package
        char c = tokens[currentIndex].charAt(0);
        if (Character.isLowerCase(c)) {
            pack(tokens, startIndex, currentIndex + 1);
        } else if (Character.isUpperCase(c)) {
            clazz(tokens, startIndex, currentIndex);
        } else {
            throw new IllegalArgumentException("Invalid token: " + tokens[currentIndex]);
        }
    }

    private void clazz(String[] tokens, int startIndex, int currentIndex) {
        //class

        char c = tokens[currentIndex].charAt(0);
        if (Character.isLowerCase(c)) {
            try {
                final Object target = getClassFromTokens(tokens, startIndex, currentIndex);
                methodOrField(tokens, currentIndex, target);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (allUpperCase(tokens[currentIndex])) {
            try {
                final Object target = getClassFromTokens(tokens, startIndex, currentIndex);
                staticField(tokens, currentIndex, target);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (Character.isUpperCase(c)) {
            clazz(tokens, startIndex, currentIndex + 1);
        } else {
            throw new IllegalArgumentException("Invalid token: " + tokens[currentIndex]);
        }
    }

    private Class<?> getClassFromTokens(String[] tokens, int startIndex, int currentIndex) throws ClassNotFoundException {
        StringBuilder classNameBuilder = new StringBuilder();
        for (int i = startIndex; i < currentIndex; i++) {
            if (i > startIndex) {
                classNameBuilder.append(".");
            }
            classNameBuilder.append(tokens[i]);
        }
        String className = classNameBuilder.toString();
        return Class.forName(className);
    }

    private void methodOrField(String[] tokens , int currentIndex, Object target) {
        //method or field
        char c = tokens[currentIndex].charAt(0);
        int i1 = tokens[currentIndex].indexOf('(');
        int i2 = tokens[currentIndex].indexOf(')');
        if (Character.isLowerCase(c) && i1 != -1 && i2 != -1 && i2 > i1) {
            method(tokens, currentIndex, target);
        } else if (allUpperCase(tokens[currentIndex]) && i1 == -1 && i2 == -1) {
            staticField(tokens, currentIndex, target);
        } else if (Character.isLowerCase(c) && i1 == -1 && i2 == -1) {
            field(tokens, currentIndex, target);
        } else {
            throw new IllegalArgumentException("Invalid token: " + tokens[currentIndex]);
        }
    }

    private static boolean allUpperCase(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    private void method(String[] tokens, int currentIndex, Object target) {
        //method
        Method method = methodFromToken(tokens[currentIndex], target);
        String[] parameters = extractParameters(tokens[currentIndex]);
        Object newTarget;
        try {
            newTarget = method.invoke(target, (Object[]) parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }

        if (newTarget != null) {
            methodOrField(tokens, currentIndex + 1, newTarget);
        }
    }

    private Method methodFromToken(String token, Object target) {
        Class<?> clazz = target instanceof Class<?> ? (Class<?>) target : target.getClass();
        Method method;
        try {
            String methodName = extractMethodName(token);
            Class<?>[] parameters = extractParameterClasses(token);
            method = clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        method.setAccessible(true);
        return method;
    }

    private String extractMethodName(String token) {
        int index = token.indexOf('(');
        if (index == -1) {
            throw new IllegalArgumentException("Invalid method token: " + token);
        }
        return token.substring(0, index);
    }

    private String[] extractParameters(String token) {
        int index1 = token.indexOf('(');
        if (index1 == -1) {
            throw new IllegalArgumentException("Invalid method token: " + token);
        }
        int index2 = token.indexOf(')');
        if (index2 == -1) {
            throw new IllegalArgumentException("Invalid method token: " + token);
        }
        String parameters = token.substring(index1 + 1, index2);
        return tokenize(parameters, ",");
    }

    private Class<?>[] extractParameterClasses(String token) {
        String[] parameters = extractParameters(token);
        return Arrays.stream(parameters).map(String::getClass).toArray(Class<?>[]::new);
    }

    private void field(String[] tokens, int currentIndex, Object target) {
        //field
        Field field = fieldFromToken(tokens[currentIndex], target);
        Object newTarget;
        try {
            newTarget = field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }

        if (newTarget != null) {
            methodOrField(tokens, currentIndex + 1, newTarget);
        }
    }

    private Field fieldFromToken(String token, Object target) {
        Class<?> clazz = target instanceof Class<?> ? (Class<?>) target : target.getClass();
        Field field;
        try {
            field = clazz.getDeclaredField(token);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
        field.setAccessible(true);
        return field;
    }

    private void staticField(String[] tokens, int currentIndex, Object target) {
        //static field

    }
}
