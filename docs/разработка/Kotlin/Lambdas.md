anonymous function that we can trat as values

```Kotlin
val calculateSquare: (Int) -> Int = { value -> value * value }
```

переменная имеющая тип фукнции это реализация интерфейса FunctionN

```Kotlin
val calculateSquare = object : Function1<Int, Int> {  
    override fun invoke(value: Int): Int = value * value
}
```

Ключевое слово `return` в лямбда-выражениях производить выход из функции в которой вызывается эта лямба, это называется [[non-local return]]

## Lambdas with Receiver

used to acces the property of the receiver without any additional line of code

```Kotlin
fun build(action: (StringBuilder).() -> Unit): String
```


