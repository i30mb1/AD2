package n7.ad2.data.source.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import n7.ad2.base.BaseDao
import n7.ad2.data.source.local.model.LocalGuide

@Dao
interface GuideDao : BaseDao<LocalGuide> {

    @Query("SELECT rowid,* FROM LocalGuides WHERE name =:heroName")
    suspend fun getGuide(heroName: String): LocalGuide

    @Query("DELETE FROM LocalGuides WHERE name=:heroName")
    suspend fun deleteGuidesFor(heroName: String)

    @Transaction
    suspend fun insertGuideAndDeleteOldGuides(localGuide: LocalGuide) {
        deleteGuidesFor(localGuide.name)
        insert(localGuide)
    }

}