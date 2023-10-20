это механизм, который позволяет выйти из внешней функции или [[Lambdas]]-выражения и вернуться к вызывающему коду, обходя оставшуюся часть
текущей функции или лямбда-выражения

```Kotlin
inline fun inlineFUnctionWithLambda(lambda: () -> Unit) {  
    println("before")  
    lambda()  
    println("after")  
}  
  
fun main() {  
    inlineFUnctionWithLambda {  
        println("inside")  
        return  
    }  
    println("END")  
}
```

Вывод будет таким

```Kotin
before
inside
```

`crossinline`— ключевое слово, которое используется для указания, что лямбда-выражение**не может содержать нелокальных**`return`, даже если
оно передано в inline-функцию. Чтобы выйти из этой лямбды нужно будет использовать метку

```Kotlin
inlineFUnctionWithLambda {  
    println("inside")  
    return@inlineFUnctionWithLambda  
}
```

Можно использовать метки для обозначения выхода

```Kotlin
fun main() {  
    val list = listOf(1,2,3)  
    list.forEach {  
        if (it == 2) return@forEach  
        println(it)  
    }  
    println("END")  
}
```

Вывод будет таким

```Kotin
1
3
END
