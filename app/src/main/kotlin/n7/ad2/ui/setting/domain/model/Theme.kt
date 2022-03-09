package n7.ad2.ui.setting.domain.model

class Red
class Purple
class Blue

enum class Theme(val key: String, val componentClass: Class<*>) {
    RED("RED", Red::class.java),
    PURPLE("PURPLE", Purple::class.java),
    BLUE("BLUE", Blue::class.java),
}