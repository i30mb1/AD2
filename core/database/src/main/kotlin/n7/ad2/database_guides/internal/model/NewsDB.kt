package n7.ad2.database_guides.internal.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [date] is unix seconds. The columns added in version 2 carry the same defaults as the migration,
 * Room compares them when it validates the table.
 */
@Entity(tableName = "NewsDB")
data class NewsDB(
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
    @ColumnInfo(name = "date", defaultValue = "0")
    val date: Long = 0,
    @ColumnInfo(name = "source", defaultValue = "")
    val source: String = "",
    @ColumnInfo(name = "author", defaultValue = "")
    val author: String = "",
    @ColumnInfo(name = "isOfficial", defaultValue = "0")
    val isOfficial: Boolean = false,
)
