package n7.ad2.repositories

import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.internal.model.LocalGuide
import javax.inject.Inject

class GuideRepository @Inject constructor(
    private val appDatabase: AppDatabase,
) {

    suspend fun insertGuide(localGuide: LocalGuide): Long {
        return appDatabase.guidesDao.insert(localGuide)
    }

    suspend fun insertGuideAndDeleteOldGuides(localGuide: List<LocalGuide>) {
        return appDatabase.guidesDao.insertGuideAndDeleteOldGuides(localGuide)
    }

}