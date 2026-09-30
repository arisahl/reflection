package se.omegapoint.ari.reflection.facit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

public final class HiddenTest {

    @Test
    public void testSecret() throws Exception {
        // given
        Field f = Hidden.class.getDeclaredField("SECRET");
        f.setAccessible(true);

        // when
        Object r = f.get(null);

        // then
        Assertions.assertEquals("The cake is a lie", r);
    }

    @Test
    public void testSideEffect() throws Exception {
        // given
        Hidden h = new Hidden();
        h.someSideEffect();
        Field f = Hidden.class.getDeclaredField("sideEffect");
        f.setAccessible(true);

        // when
        Object r = f.get(h);

        // then
        Assertions.assertTrue(r.toString().startsWith("I have been called at"));
    }

    @Test
    public void testInternalStuff() throws Exception {
        // given
        Hidden h = new Hidden();
        Method m = Hidden.class.getDeclaredMethod("doInternalStuff");
        m.setAccessible(true);
        Field f = Hidden.class.getDeclaredField("internalStuff");
        f.setAccessible(true);

        // when
        m.invoke(h);
        Object r = f.get(h);

        // then
        Assertions.assertTrue(r.toString().startsWith("Doing internal stuff at"));
    }

    @Test
    public void testSomethingWeWantToMock() {
        // given
        Hidden hiddenMock = Mockito.mock(Hidden.class);
        when(hiddenMock.somethingWeWantToMock()).thenReturn("Mocked something");

        // when
        String result = hiddenMock.somethingWeWantToMock();

        // then
        Assertions.assertEquals("Mocked something", result);
    }
}