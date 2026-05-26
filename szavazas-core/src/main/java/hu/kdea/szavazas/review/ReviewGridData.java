package hu.kdea.szavazas.review;

import java.util.List;

public record ReviewGridData(int columnCount, int rowCount, String voteName, List<ReviewCellData> cells, List<ReviewNonconformityData> nonconformities) {
}
