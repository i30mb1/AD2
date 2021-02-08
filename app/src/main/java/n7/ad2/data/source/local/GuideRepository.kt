package n7.ad2.data.source.local

import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalGuide
import javax.inject.Inject

class GuideRepository @Inject constructor(
    private val appDatabase: AppDatabase,
) {

    suspend fun insertGuide(localGuide: LocalGuide): Long {
        return appDatabase.guideDao.insert(localGuide)
    }

    suspend fun insertGuideAndDeleteOldGuides(localGuide: List<LocalGuide>) {
        return appDatabase.guideDao.insertGuideAndDeleteOldGuides(localGuide)
    }

}