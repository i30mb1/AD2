package n7.ad2.feature.camera.domain.impl.processing

enum class MLModelName(val officialName: String) {
    TFACE_LITE("tfacelite"),
    HEADPOSE_OCCLUSION("headpose_occlusion"),
    BLAZE_FACE("blazeface");

    companion object {
        private val map = entries.associateBy { it.officialName }
        infix fun from(officialName: String): MLModelName = map[officialName] ?: error("Unknown ML model: $officialName")
    }
}
