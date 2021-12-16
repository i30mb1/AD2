package n7.ad2.database_guides.internal.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocalHeroes")
data class LocalHero(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "mainAttr")
    val mainAttr: String,
    @ColumnInfo(name = "viewedByUser")
    val viewedByUser: Boolean,
)