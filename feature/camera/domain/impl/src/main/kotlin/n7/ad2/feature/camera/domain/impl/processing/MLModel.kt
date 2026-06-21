package n7.ad2.feature.camera.domain.impl.processing

sealed interface MLModel {
    val interpreter: MLInterpreter
    val params: Params

    class HeadPose(override val interpreter: MLInterpreter, override val params: Params) : MLModel
    class TFaceLite(override val interpreter: MLInterpreter, override val params: Params) : MLModel
    class BlazeFace(override val interpreter: MLInterpreter, override val params: Params) : MLModel

    interface Params {
        val name: MLModelName
        val versionString: String
        val nameString: String get() = name.officialName
    }
}

data class ParamsImpl(override val name: MLModelName, override val versionString: String) : MLModel.Params
