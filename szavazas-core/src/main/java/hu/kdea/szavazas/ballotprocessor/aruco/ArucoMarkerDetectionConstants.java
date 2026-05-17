package hu.kdea.szavazas.ballotprocessor.aruco;

import java.util.Set;

public interface ArucoMarkerDetectionConstants {
    int TOP_LEFT_ID = 37;
    int TOP_RIGHT_ID = 50;
    int BOTTOM_LEFT_ID = 44;
    int BOTTOM_RIGHT_ID = 219;
    Set<Integer> REQUIRED_IDS = Set.of(TOP_LEFT_ID, TOP_RIGHT_ID, BOTTOM_LEFT_ID, BOTTOM_RIGHT_ID);
}
