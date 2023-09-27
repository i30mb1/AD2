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
fun <T> Example(variable: T) where T : Int, T : Short {
    ...
}
```

## Invariance

```
abstract class Car                      //Parent  
  
class Audi : Car()                      //Child  
class Mercedes : Car()                  //Child  
  
class CarMechanic<T: Car>  
  
fun main() {  
    val mechanic = CarMechanic<Audi>()      //From now on, type of this property  
    //is CarMechanic<Audi> and nothing    //else can be assigned to it.  
    val genericMechanic: CarMechanic<Car> = mechanic //👈🏻 This is invalid. 
}
```

## Covariance — out

```Kotlin
abstract class Car //Parent  

class Audi : Car() //Child  
class Mercedes : Car() //Child  

class CarMechanic<out T : Car> { //👈🏻 This is where the magic happens.  
    fun repair() {}
}

class Workshop {
    fun addMechanic(mechanic: CarMechanic<Car>) {} //This fun wants us to pass  
} //a CarMechanic of type Car  

//which is our parent.  
fun main() {
    val mechanic = CarMechanic<Audi>()
    val workshop = Workshop()

    workshop.addMechanic(mechanic)
}
```

https://medium.com/@sevbanbuyer/lets-stretch-with-kotlin-generics-d721dad9e012
