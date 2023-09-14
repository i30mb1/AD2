are a mechanism to add extra information or metadata to your code, without affecting the runtime behavior

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
@JvmStatic - expose Kotlin object methods as static methods in Java
@Nullable / @NotNull - explicitly indicate a nullable type for Java interrop
