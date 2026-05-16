// QRPreprocessingPipeline.kt
package hu.kdea.szavazas.ballotprocessor.qrpreprocess

import boofcv.struct.image.GrayU8
import hu.kdea.szavazas.ballotprocessor.ImageSaver

class QRPreprocessingPipeline(
    private val debugSaver: ImageSaver?,   // nullable – no saving if null
    private val steps: List<PreprocessingStep>
) {
    fun execute(input: GrayU8, baseName: String): GrayU8 {
        var current = input
        steps.forEachIndexed { i, step ->
            current = step.process(current)
            debugSaver?.save(current, "${baseName}_${i}_${step.name}.jpg")
        }
        return current
    }
}