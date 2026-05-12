package hu.kdea.szavazas

import org.opencv.core.Mat

interface DebugImageSaver {
    fun save(mat: Mat, fileName: String)
}