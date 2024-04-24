package n7.ad2.feature.camera.domain

import java.io.File

interface Recorder {
    suspend fun init()
    suspend fun start(): File
    suspend fun stop()
}
