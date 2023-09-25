In order for [[Kotlin]]/[[Java]] to work on the JVM, it needs to be converted into Java byte-code through compilation
Java/Kotlin (TestClass.kt) -> Java Compiler (javac) / Kotlin Compiler (kotlinc) -> Java byte-code (TestClass.class) -> JVM

Android chose to create two special virtual mashines:

- DVM (Dalvik Virtual Machine) - use Just-In-Time compilation, app is compiled while its is running, which happens when the app start up
- ART (Android Runtime) - for Android 4.4 and above use Ahead-Of-Time compilation, app is compiled when app is being installed

Java/Kotlin (TestClass.kt) -> Java Compiler (javac) / Kotlin Compiler (kotlinc) -> Java byte-code (TestClass.class) -> DEX Compiler (DX) ->
Dalvik Bytecode (classes.dex) -> DVM -> Machine Code
