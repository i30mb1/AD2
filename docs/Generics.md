в [[Kotlin]]

<T,N> - при отсутствии логики по умолчанию первое значение это возвращаемое а второе принимаемое

```Kotlin
interface InOutExample<T> {  
    fun consumer(t: T)  
    fun producer(): T  
    fun both(t: T): T  
}
```

```Kotlin
interface OutExample<out T> {  
    fun consumer(t: T)
    fun producer(): T  
    fun both(t: T): T  
}
```

```Kotlin
interface InExample<in T> {  
    fun consumer(t: T)  
    fun producer(): T  
    fun both(t: T): T  
}
```

```Kotlin
fun <T> Example(variable: T) where T: Int, T: Short { ... }
```
