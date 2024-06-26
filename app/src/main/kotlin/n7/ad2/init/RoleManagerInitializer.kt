package n7.ad2.init

import android.app.Application
import android.app.role.RoleManager
import androidx.core.content.getSystemService
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger

class RoleManagerInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        val roleManager = app.getSystemService<RoleManager>()
        val isRoleBrowserHeld = roleManager?.isRoleHeld(RoleManager.ROLE_BROWSER) ?: false
        logger.log("is_role_browser_held = $isRoleBrowserHeld")
    }
}
