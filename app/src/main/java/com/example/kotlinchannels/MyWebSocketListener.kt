package com.example.kotlinchannels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class MyWebSocketListener: WebSocketListener() {

    private val socketListener: Channel<String> = Channel()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send("Yo")
        webSocket.send("Yo!")
        webSocket.send("Honey")
        webSocket.send("Singh")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socketListener.send("Android")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socketListener.send("iOS")
        }
        socketListener.close()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        CoroutineScope(Dispatchers.IO).launch {
            socketListener.send("Failure")
        }
    }
}