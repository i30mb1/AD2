package n7.ad2.init

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.logger.Logger
import java.io.PrintWriter
import java.io.StringWriter

class CrashHandlerInitializer : Initializer {

    override fun init(app: Application, logger: Logger, appInformation: AppInformation) {
        val defaultEUH = Thread.getDefaultUncaughtExceptionHandler()
        val customEUH = CrashHandler(defaultEUH, logger)
        Thread.setDefaultUncaughtExceptionHandler(customEUH)
    }

    private class CrashHandler(
        private val defaultEUH: Thread.UncaughtExceptionHandler?,
        private val logger: Logger,
    ) : Thread.UncaughtExceptionHandler {

        override fun uncaughtException(thread: Thread, exception: Throwable) {
            val stackTrace = StringWriter()
            exception.printStackTrace(PrintWriter(stackTrace))

            logger.log(exception.toString())

            defaultEUH?.uncaughtException(thread, exception)
        }

    }

}