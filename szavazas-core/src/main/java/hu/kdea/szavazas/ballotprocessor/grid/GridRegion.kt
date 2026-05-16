package hu.kdea.szavazas.ballotprocessor.grid

import boofcv.struct.image.GrayU8

data class GridRegion(val projectionInput: GrayU8, val cropTop: Int, val qrCentreX: Int)