package n7.ad2.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocalHeroes")
data class LocalHero(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "rowid")
        val id: Long = 0,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "assetsPath")
        val assetsPath: String,
        @ColumnInfo(name = "mainAttr")
        val mainAttr: String
)