В языке Kotlin есть возможность работать с функциями как с объектами. Функции можно сохранять в переменные, передавать как аргументы и
возвращать из других функций

```Kotlin
class Person(private val name: String) {  
    fun getName(): String = name  
}  
  
val persons = listOf(Person("Luffy"))  
persons.map(Person::getName)
```
