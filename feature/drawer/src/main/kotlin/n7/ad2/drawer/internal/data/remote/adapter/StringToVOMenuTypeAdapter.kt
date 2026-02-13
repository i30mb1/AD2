package n7.ad2.drawer.internal.data.remote.adapter

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import n7.ad2.drawer.internal.data.remote.model.VOMenuType

@Retention(AnnotationRetention.RUNTIME)
annotation class StringVoMenuType

internal class StringVOMenuTypeAdapter : KSerializer<VOMenuType> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VOMenuType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): VOMenuType {
        val string = decoder.decodeString()
        return try {
            VOMenuType.valueOf(string)
        } catch (e: Exception) {
            VOMenuType.UNKNOWN
        }
    }

    override fun serialize(encoder: Encoder, value: VOMenuType) {
        encoder.encodeString(value.name)
    }
}
