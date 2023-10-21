package n7.ad2.heroes.domain.internal.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HeroesTable")
data class HeroDatabase(
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
