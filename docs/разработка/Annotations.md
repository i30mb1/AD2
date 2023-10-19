are a mechanism in [[Kotlin]] to add extra information or metadata to your code, without affecting the runtime behavior

**Creating Custom Annotations**

- Create Class

```
annotation class MyCustomAnnotation()
```

- Target Annotation to Element

```
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class MyCustomAnnotation()
```

- Retention Policies - determine how long the annotated information will be available
    - RUNTIME (Default) - available in runtime
    - BINARY - available only in compile time, visible for reflection
    - SOURCE - available only in compile time, used to provide hint during compilation

```
@Retention(AnnotationRetention.RUNTIME)  
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class MyCustomAnnotation()
```

@OptIn - is used to explicitly opt into language features or experimental api that may not be enabled by default
@Metadata - generated data that attached to Kotlin classes, objects, functions by the Kotlin compiler
@Deprecated - is used to mark elements of code that are outdate and will be removed if future
@Nullable / @NotNull - explicitly indicate a nullable type for Java interrop

@Volatile
@Synchronized
@Strictfp
@Transient
@JvmOverloads - генерирует все конструкторы для Java
@JvmStatic - делаем поле или метод static для Java (expose Kotlin object methods as static methods in Java)
get:@JvmName("name") - делаем имя переменной для Java
@JvmField - делает поле игнорируя set'еры и get'еры для Java
@JvmSupressWildcards - отключаем wildcard для методов чтобы не было возможности передать любых обьектов из Java в Kotlin
@JvmWildcard - для возращаемых типов генерирует тип ? для Java
@Throws(Exception::class) - меняет инофрмацию что метод кидает ошибку для Java

```Kotlin
fun List<Int>.printReversedSum() {  
    println("")  
  
}  
  
@JvmName("printReversedConcatenation")  
fun List<String>.printReversedSum() {  
    println("")  
}
```

переименовываем класс для Java чтобы все Static были в одном пакете

```Kotlin
@file:JvmName("File")
@file:JvmMultifileClass 
```

https://youtu.be/1L0q5VKx_-s?list=LL
