package hu.kdea.szavazas

import nu.pattern.OpenCV
import org.opencv.core.Core

class DesktopOpenCVLoader : IOpenCVLoader {
    override fun load() {
        OpenCV.loadShared()
        Core.getVersionMajor()  // force check
    }
}