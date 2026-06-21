import com.google.common.truth.Truth.assertThat
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.FaceDetector
import n7.ad2.feature.camera.domain.impl.processing.detect.switchable
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ImageMetadata
import org.junit.Test

class FaceDetectSwitchableTest {

    private val image = Image(Any(), ImageMetadata(1, 1, false))

    private fun constDetect(probability: Float): FaceDetect = object : FaceDetect {
        override fun detect(image: Image): List<DetectedFaceNormalized> =
            listOf(DetectedFaceNormalized(0f, probability, 0f, probability, probability))
    }

    private fun crashDetect(): FaceDetect = object : FaceDetect {
        override fun detect(image: Image): List<DetectedFaceNormalized> = error("boom")
    }

    @Test
    fun `WHEN created THEN delegates to first detector`() {
        val switchable = linkedMapOf<FaceDetector, () -> FaceDetect>(
            FaceDetector.BLAZE_FACE to { constDetect(0.9f) },
            FaceDetector.TFACE_LITE to { constDetect(0.5f) },
        ).switchable(Logger())

        assertThat(switchable.current.value).isEqualTo(FaceDetector.BLAZE_FACE)
        assertThat(switchable.detect(image).single().probability).isEqualTo(0.9f)
    }

    @Test
    fun `WHEN select THEN delegates to selected detector`() {
        val switchable = linkedMapOf<FaceDetector, () -> FaceDetect>(
            FaceDetector.BLAZE_FACE to { constDetect(0.9f) },
            FaceDetector.TFACE_LITE to { constDetect(0.5f) },
        ).switchable(Logger())

        switchable.select(FaceDetector.TFACE_LITE)

        assertThat(switchable.current.value).isEqualTo(FaceDetector.TFACE_LITE)
        assertThat(switchable.detect(image).single().probability).isEqualTo(0.5f)
    }

    @Test
    fun `WHEN detector throws THEN returns empty without crash`() {
        val switchable = linkedMapOf<FaceDetector, () -> FaceDetect>(
            FaceDetector.BLAZE_FACE to { crashDetect() },
        ).switchable(Logger())

        assertThat(switchable.detect(image)).isEmpty()
    }
}
