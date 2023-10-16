package n7.ad2.nativesecret

class NativeSecretExtractor {

    external fun printHelloWorld(): String

    companion object {
        init {
            System.loadLibrary("secret")
        }
    }
}
