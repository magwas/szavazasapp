package hu.kdea.szavazas

import boofcv.struct.image.GrayU8

data class PreprocessResult(val scaledGray: GrayU8, val markerTopY: Double?)