package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

data class GridRegion(val projectionInput: GrayU8, val cropTop: Int, val qrCentreX: Int)