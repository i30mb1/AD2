package n7.ad2

private data class AD2Log(val message: String)

class AD2Logger {

    private val logList: MutableList<AD2Log> = mutableListOf()

    fun addToLog(text: String) {
        val log = AD2Log(text)
        logList.add(log)
    }

}