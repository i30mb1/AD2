package n7.ad2.nativesecret

class NativeSecretExtractor {

    init {
        System.loadLibrary("secret")
    }

    external fun printHelloWorld(): String
}
