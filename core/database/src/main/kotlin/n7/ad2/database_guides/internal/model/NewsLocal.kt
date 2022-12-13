package n7.ad2.database_guides.internal.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NewsTable")
data class NewsLocal(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowId")
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "urlImage")
    val urlImage: String,
    @ColumnInfo(name = "loadedFromPage")
    val loadedFromPage: Int,
    @ColumnInfo(name = "href")
    val href: String,
)