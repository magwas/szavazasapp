package io.github.magwas.konveyor.testing;

import org.junit.Before;

public class TestBase {

    public static String environmentState;

    @Before
    public void resetEnvironmentState() throws Throwable {
        environmentState = null;
        setUp();
    }

    public void given(String newState) {
        environmentState = newState;
    }

    public void setUp() throws Throwable {
    }
}
