package hu.kdea.szavazas

class AndroidOpenCVLoader : IOpenCVLoader {
    override fun load() {
        System.loadLibrary("opencv_java4")
    }
}