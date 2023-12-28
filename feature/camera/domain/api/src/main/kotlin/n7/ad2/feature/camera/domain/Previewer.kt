package n7.ad2.feature.camera.domain

interface Previewer {
    suspend fun start(surface: Any)
}
