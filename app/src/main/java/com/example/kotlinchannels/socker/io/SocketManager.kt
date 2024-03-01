package com.example.kotlinchannels.socker.io

import com.example.kotlinchannels.constants.AppConstants
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketManager {

    private var socket: Socket? = null
    private val dummyUrl: String = "url"

    init {
        try {
            socket = IO.socket(dummyUrl)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }

    fun onMessageReceived(listener: (String?) -> Unit) {
        socket?.on(AppConstants.MESSAGE) { args ->
            val message = args[0] as? String
            listener.invoke(message)
        }
    }

    fun sendMessage(message: String) {
        socket?.emit(AppConstants.MESSAGE, message)
    }
}