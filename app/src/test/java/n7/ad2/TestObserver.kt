package n7.ad2

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.test(): TestObserver<T> {
    return TestObserver.test(this)
}

class TestObserver<T> private constructor() : Observer<T> {

    companion object {
        fun <T> test(liveData: LiveData<T>): TestObserver<T> {
            val observer = TestObserver<T>()
            liveData.observeForever(observer)
            return observer
        }
    }

    private val valueHistory = ArrayList<T>()
    private val valueLatch = CountDownLatch(1)

    override fun onChanged(value: T) {
        valueHistory.add(value)
        valueLatch.countDown()
    }

    fun value(time: Long = 2): T {
        if (!valueLatch.await(2, TimeUnit.SECONDS)) throw TimeoutException("LiveData value was never set.")
        return valueHistory.last()
    }

    fun valueHistory(): List<T> {
        return valueHistory.toList()
    }

}