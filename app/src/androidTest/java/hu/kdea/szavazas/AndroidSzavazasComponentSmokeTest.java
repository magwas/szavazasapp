package hu.kdea.szavazas;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AndroidSzavazasComponentSmokeTest {
    @Test
    public void buildInstantiatesTheAndroidProductionDaggerGraphFromUnitTests() {
        Context context = ApplicationProvider.getApplicationContext();
        AndroidSzavazasComponent androidSzavazasComponent = DaggerAndroidSzavazasComponent.builder()
            .context(context)
            .build();

        assertNotNull(androidSzavazasComponent);
        assertNotNull(androidSzavazasComponent.ballotProcessingApi());
        assertNotNull(androidSzavazasComponent.saveBallotResultService());
        assertNotNull(androidSzavazasComponent.prepareReviewGridService());
    }
}
