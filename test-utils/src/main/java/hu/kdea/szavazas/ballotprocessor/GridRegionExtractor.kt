package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

class GridRegionExtractor {
    fun extract(
        scaledGray: GrayU8,
        qrCentreX: Int,
        qrBottomY: Int,
        markerTopY: Double?
    ): GridRegion? {
        val inverted = Inverter.invert(scaledGray)
        val proj = RowProjectionComputer.compute(inverted)
        val (cropTop, cropBottom) =
            GridBoundaryFinder.find(proj, qrBottomY, markerTopY) ?: return null
        val cropWidth = scaledGray.width - qrCentreX
        val cropHeight = cropBottom - cropTop + 1
        val cropped = Crop.crop(scaledGray, qrCentreX, cropTop, cropWidth, cropHeight)
        val binary = ImageNormalizer.normalizeAndThreshold(cropped)
        return GridRegion(binary, cropTop, qrCentreX)
    }
}