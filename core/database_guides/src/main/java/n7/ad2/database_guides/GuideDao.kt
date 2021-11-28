package n7.ad2.database_guides

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import n7.ad2.database_guides.model.LocalGuide

@Dao
interface GuideDao : BaseDao<LocalGuide> {

    @Query("SELECT rowid,* FROM LocalGuides WHERE name =:heroName")
    suspend fun getGuide(heroName: String): LocalGuide

    @Query("DELETE FROM LocalGuides WHERE name=:heroName")
    suspend fun deleteGuidesFor(heroName: String)

    @Transaction
    suspend fun insertGuideAndDeleteOldGuides(localGuide: List<LocalGuide>) {
        deleteGuidesFor(localGuide[0].name)
        insert(localGuide)
    }

}