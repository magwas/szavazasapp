package hu.kdea.szavazas.ballotprocessor.qr.preprocess

import boofcv.struct.image.GrayU8

interface PreprocessingStep {
    val name: String
    fun process(input: GrayU8): GrayU8
}