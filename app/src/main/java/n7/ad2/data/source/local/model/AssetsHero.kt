package n7.ad2.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class AssetsHero(
    @Json(name = "assetsPath")
    val assetsPath: String = "",
    @Json(name = "mainAttr")
    val mainAttr: String = "",
    @Json(name = "nameEng")
    val name: String = ""
)