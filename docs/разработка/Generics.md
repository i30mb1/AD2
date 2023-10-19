в [[Kotlin]]

<T,N> - при отсутствии логики по умолчанию первое значение это возвращаемое а второе принимаемое

Вариантность - описывает как обобщенные типы, типизированные классами из одной иерархии наследования, соотносятся друг с другом. 3 типа:

## Ковариативность

сохранение иерархии наследования исходных типов в производных типах в том же порядке
в [[Java]] массивы ковариантными, мы можем присвоить массиву обьектов массив строк, и при попытке добавить туда строку мы упадем

```Kotlin
open class Animal  
class Dog: Animal()  
class Cage<in T : Animal>  
  
val cageForDog = Cage<Dog>()  
val cageForAnimal: Cage<Animal> = cageForDog
```

```Kotlin
interface InOutExample<T> {  
    fun consumer(t: T)  
    fun producer(): T  
    fun both(t: T): T  
}
```

## Контравариативность

обращение иерархии исходных типов на противоположную в производных типах
мы можем отдать меньше чем надо

```Kotlin
interface Producer<out T> {  
    fun produce(): T  
}  
  
val stringProducer: Producer<String> = object : Producer<String> {  
    override fun produce(): String = "!"  
}  
val anyProducer: Producer<Any> = stringProducer
```

``

```Kotlin
interface OutExample<out T> {  
    fun consumer(t: T)
    fun producer(): T  
    fun both(t: T): T  
}
```

## Инвариативность

```Kotlin
interface InExample<T> {
    fun consumer(t: T)
    fun producer(): T
    fun both(t: T): T
}
```

```Kotlin
fun <T> Example(variable: T) where T : Int, T : Short {
    ...
}
```

```Kotlin
abstract class Car                      //Parent  
  
class Audi : Car()                      //Child  
class Mercedes : Car()                  //Child  
  
class CarMechanic<T: Car>  
  
fun main() {  
    val mechanic = CarMechanic<Audi>()      //From now on, type of this property  
    //is CarMechanic<Audi> and nothing else can be assigned to it.  
    val genericMechanic: CarMechanic<Car> = mechanic //👈🏻 This is invalid. 
}

```

@UnsafeVariance - используем когда хотим игнорировать предупреждения компилятора
но можно использовать и <*>

https://medium.com/@sevbanbuyer/lets-stretch-with-kotlin-generics-d721dad9e012
