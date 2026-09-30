package se.omegapoint.ari.reflection.facit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {
    static void main() {
        helloWorld();

        try {
            helloReflectedWorld();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void helloWorld() {
        System.out.println("Hello World");
    }

    private static void helloReflectedWorld() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //System.out.println("Hello Reflected World");

        Class<?> clazz = Class.forName("java.lang.System");
        //System.out.println(clazz);

        Field field = clazz.getField("out");
        //System.out.println(field);

        Object object = field.get(clazz);
        //System.out.println(object);

        Method[] methods = object.getClass().getMethods();
        //Arrays.stream(methods).filter(m -> m.getName().startsWith("println")).forEach(System.out::println);

        Method method = object.getClass().getMethod("println", String.class);
        //System.out.println(method);

        method.invoke(object, "Hello Reflected World");
    }
}
