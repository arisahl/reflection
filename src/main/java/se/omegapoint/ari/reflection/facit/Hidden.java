package se.omegapoint.ari.reflection.facit;

import java.time.Instant;

public final class Hidden {
    private static final String SECRET = "The cake is a lie";
    private String sideEffect = null;
    private String internalStuff = null;

    public void someSideEffect() {
        sideEffect = "I have been called at "+ Instant.now() +"!";
    }

    private void doInternalStuff() {
        internalStuff = "Doing internal stuff at "+ Instant.now() +"!";
    }

    public String somethingWeWantToMock() {
        return "This is something we want to mock";
    }
}