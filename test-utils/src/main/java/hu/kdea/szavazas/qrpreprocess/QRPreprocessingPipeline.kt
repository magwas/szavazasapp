// QRPreprocessingPipeline.kt
package hu.kdea.szavazas.qrpreprocess

import boofcv.struct.image.GrayU8
import hu.kdea.szavazas.FileDebugImageSaver

class QRPreprocessingPipeline(
    private val debugSaver: FileDebugImageSaver,   // now concrete
    private val steps: List<PreprocessingStep>
) {
    fun execute(input: GrayU8, baseName: String): GrayU8 {
        var current = input
        steps.forEachIndexed { i, step ->
            current = step.process(current)
            debugSaver.save(current, "${baseName}_${i}_${step.name}.jpg")
        }
        return current
    }
}