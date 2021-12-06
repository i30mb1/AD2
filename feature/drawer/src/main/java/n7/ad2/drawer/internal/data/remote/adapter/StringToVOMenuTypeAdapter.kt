package n7.ad2.drawer.internal.data.remote.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import n7.ad2.drawer.internal.data.remote.model.VOMenuType

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class StringVoMenuType

class StringVOMenuTypeAdapter {

    @ToJson
    fun toJson(@StringVoMenuType value: VOMenuType): String {
        return value.name
    }

    @FromJson
    fun fromJson(value: String): VOMenuType = try {
        VOMenuType.valueOf(value)
    } catch (e: Exception) {
        VOMenuType.UNKNOWN
    }

}