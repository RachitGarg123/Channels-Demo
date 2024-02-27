package com.example.kotlinchannels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// Channels are used to communicate between coroutines asynchronously so that the underlying thread doesn't get blocked

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<AppCompatButton>(R.id.btnEmitValue).setOnClickListener {
//            channel(lifecycleScope)
            receiveChannel(lifecycleScope)
        }
    }

/*     This is an example of Rendezvous Channel since it has a capacity of 0.
     Both consumer and producer should be present for the coroutine to communicate.
     If either is not present then the other gets suspended and resumes only when it is present to communicate*/
    private fun channel(coroutineScope: CoroutineScope) {
        val channel: Channel<String> = Channel()
        coroutineScope.launch {
//            delay(3000)
            Log.i("Channel", "Send ----> Element 1")
            channel.send("Element 1")
//            delay(3000)
            Log.i("Channel", "Send ----> Element 2")
            channel.send("Element 2")
            channel.close()
        }
        coroutineScope.launch {
//            delay(3000)
            channel.consumeEach {
//                delay(3000)
                Log.i("Channel", "Received ----> $it")
            }
            Log.i("Channel", "Done!!") // this will be printed when channel will be closed
//            channel.send("Sending Element 3")
        }

    }
    /*If we are sending two values through a channel then 1st element is sent and received and
    after that 2nd element will be sent and received and then Done!! will be printed*/

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun receiveChannel(coroutineScope: CoroutineScope) {
        var channel: ReceiveChannel<String> = Channel() // Receive Channel cannot send data back
        coroutineScope.launch {
            // produces or emits a stream of values and returns an instance of ReceivedChannel to receive
            // produce is preferred instead of sending data through normal channel.send() because it automatically closes the channels after emitting values.
            channel = produce {
                send("A")
                send("B")
                send("C")
                send("D")
            }
        }
        coroutineScope.launch {
            val value1 = channel.receive() // use for receiving individual value
            Log.i("ReceivedChannel", "value ----> $value1")
            channel.consumeEach {
                Log.i("ReceivedChannel", "Received ----> $it")
            }
        }
        Log.i("ReceivedChannel", "${channel.isClosedForReceive}") // true { without using close() in case of receive channel}
    }

    private fun streamNumbers(scope: CoroutineScope) {
        scope.launch {
            val numbers = produceNumbers(count = 20)
            val result = pipeline(numbers)
        }
    }

    /* this function is a representation of a pipeline since it has 4 stages
        - filter
        - mapping
        - reduce
        - send */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.pipeline(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        val filtered = filter(numbers) { it % 2 != 0 }
        val sqauared = map(filtered) { it * it}
        val sum = reduce(sqauared) {acc, x -> acc + x}
        send(sum)
    }

    fun CoroutineScope.filter(
        numbers: ReceiveChannel<Int>,
        predicate: (Int) -> Boolean
    ): ReceiveChannel<Int> = produce {
        numbers.consumeEach { number ->
            if (predicate(number)) send(number)
        }
    }

    fun CoroutineScope.map(
        numbers: ReceiveChannel<Int>,
        mapper: (Int) -> Int
    ): ReceiveChannel<Int> = produce {
        numbers.consumeEach { number ->
            send(mapper(number))
        }
    }

    fun reduce(
        numbers: ReceiveChannel<Int>,
        accumulator: (Int, Int) -> Int
    ): Int = runBlocking {
        var result = 0
        for (number in numbers) {
            result = accumulator(result, number)
        }
        result
}

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.produceNumbers(count: Int): ReceiveChannel<Int> = produce {
        for(i in 1..count) {
            send(i)
        }
    }

/*     This is a buffered channel i.e it has some capacity so that it can hold some elements temporarily
     Buffered Channels are useful when there is a delay between producer and consumer as it can have some messages stored in it
     Default buffer size is 64.*/
    private fun bufferedChannel() {
        val channel = Channel<String>(capacity = 10)

    }

    // This is a ConflatedChannel i.e it will only keeps the latest value available and can only store one value
    private fun conflatedChannel() {
        val channel = Channel<String>(Channel.CONFLATED)
    }

/*     This is an UnlimitedChannel i.e no predefined size of buffer
     It can hold an unlimited no. of elements
     It can be used in places where you don't want producers to get suspended due to limited buffer size and
     where consumers can keep up with the producers rate of producing
     It can however cause memory issues in case buffer size grows too much */
    private fun unlimitedChannel() {
        val channel = Channel<String>(Channel.UNLIMITED)
    }
}