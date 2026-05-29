package io.github.magwas.konveyor.testing;

import org.junit.jupiter.api.BeforeEach;

public class TestBase {

    public static String environmentState;

    @BeforeEach
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
