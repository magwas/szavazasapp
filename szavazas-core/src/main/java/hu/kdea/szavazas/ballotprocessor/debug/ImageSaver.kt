// ImageSaver.kt
package hu.kdea.szavazas.ballotprocessor.debug

interface ImageSaver {
    /**
     * Saves a BoofCV image to persistent storage.
     *
     * @param image Supported types: [GrayU8], [Planar] (3-band GrayU8).
     * @param fileName A name hint (may be adjusted by the implementation).
     */
    fun save(image: Any, fileName: String)
}