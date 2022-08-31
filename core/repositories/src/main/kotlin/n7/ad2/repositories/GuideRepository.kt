package n7.ad2.repositories

import n7.ad2.database_guides.api.dao.GuidesDao
import n7.ad2.database_guides.internal.model.LocalGuide
import javax.inject.Inject

class GuideRepository @Inject constructor(
    private val guidesDao: GuidesDao,
) {

    suspend fun insertGuide(localGuide: LocalGuide): Long {
        return guidesDao.insert(localGuide)
    }

    suspend fun insertGuideAndDeleteOldGuides(localGuide: List<LocalGuide>) {
        return guidesDao.insertGuideAndDeleteOldGuides(localGuide)
    }

}