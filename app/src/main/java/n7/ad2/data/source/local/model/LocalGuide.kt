package n7.ad2.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.TEXT
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocalGuides")
class LocalGuide(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "json")
    val json: String,
    @ColumnInfo(typeAffinity = TEXT, name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    val timestamp: Long = 0,
)