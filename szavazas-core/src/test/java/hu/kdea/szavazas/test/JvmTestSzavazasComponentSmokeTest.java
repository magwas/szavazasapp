package hu.kdea.szavazas.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import hu.kdea.szavazas.DaggerJvmTestSzavazasComponent;
import hu.kdea.szavazas.JvmTestSzavazasComponent;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import io.github.magwas.konveyor.testing.TestBase;
import java.io.File;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@Tag("integration")
public class JvmTestSzavazasComponentSmokeTest extends TestBase {
    private JvmTestSzavazasComponent jvmTestSzavazasComponent;
    private BallotProcessingApi ballotProcessingApi;

    @Override
    public void setUp() {
        jvmTestSzavazasComponent = DaggerJvmTestSzavazasComponent.builder()
            .outputDir(new File("build/test-dagger-smoke"))
            .build();
        ballotProcessingApi = jvmTestSzavazasComponent.ballotProcessingApi();
    }

    @Test
    @DisplayName("instantiates the JVM test Dagger graph")
    public void buildInstantiatesTheJvmTestDaggerGraph() {
        assertNotNull(jvmTestSzavazasComponent);
        assertNotNull(ballotProcessingApi);
    }
}
