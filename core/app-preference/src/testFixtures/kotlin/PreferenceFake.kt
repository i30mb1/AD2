import n7.ad2.apppreference.Preference

class PreferenceFake : Preference {
    override suspend fun isNeedToUpdateSettings(): Boolean = true
    override suspend fun saveUpdateNewsDate() = Unit
    override suspend fun saveSettings(data: String) = Unit
    override suspend fun getSettings(): String = ""
    override suspend fun saveDate(date: Int) = Unit
    override suspend fun getDate(): Int = 0
    override suspend fun setFingerCoordinateEnabled(isEnabled: Boolean) = Unit
    override suspend fun isFingerCoordinateEnabled(): Boolean = true
    override suspend fun isLogWidgetEnabled(): Boolean = true
    override suspend fun isNeedToUpdateNews(): Boolean = false
}
