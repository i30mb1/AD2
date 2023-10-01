В [[Kotlin]] ключевое слово `this` используется

- для ссылки на текущий экземпляр класса

```Kotlin
class Person(val name: String) {
    fun sayHello() {
        println("Hello, my name is ${this.name}")
    }
}
```

- для ссылки на приемник переданный в качестве параметра

```Kotlin
fun Person.printName() {
    println(this.name)
}
```

- для доступа к экземпляру внешнего класса из внутреннего класса или функции

```Kotlin
class Outer {
    val name = "Outer"

    class Inner {
        fun foo() {
            println(this@Outer.name)
        }
    }
}
```
