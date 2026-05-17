package hu.kdea.szavazas.ballotprocessor;

public interface GridConstants {
    int EXPECTED_WIDTH_MIN = 20;
    int EXPECTED_WIDTH_MAX = 40;
    int MERGE_CLOSE_PEAKS_DIST = 5;
    float PEAK_THRESHOLD_MIN = 10.0f;
    float PEAK_RELATIVE_THRESHOLD = 0.8f;
    int BOUNDARY_MARGIN = 10;
    int X_MARGIN = 6;
    int ERODE_KERNEL_SIZE = 2;
    int ERODE_ITERATIONS = 0;
    int MIN_BRANCHES = 50;
    double NORMALIZE_SIZE_DIVIDER = 2.0;
    double NORMALIZE_SCALE = 1.0;
}
