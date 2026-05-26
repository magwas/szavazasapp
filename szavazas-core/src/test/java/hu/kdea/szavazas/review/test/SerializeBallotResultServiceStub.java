package hu.kdea.szavazas.review.test;

import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.review.SerializeBallotResultService;

public final class SerializeBallotResultServiceStub {
    public static SerializeBallotResultService stub() {
        return new SerializeBallotResultService(new LoggerWrapper());
    }
}
