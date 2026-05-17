package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import io.github.magwas.konveyor.annotations.Glue;

@Glue
public interface BallotProcessingApi {
    BallotProcessingOutcomeData apply(Planar<GrayU8> planar);
}
