package n7.ad2.nativesecret

class NativeSecretExtractor {

    init {
        System.loadLibrary("extractor")
    }

    external fun printHelloWorld(): String
}
