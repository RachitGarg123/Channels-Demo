package com.example.kotlinchannels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinchannels.socker.io.SocketManager

class SocketIOActivity : AppCompatActivity() {

    private val socketManager = SocketManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket_io)
        socketManager.connect()
        socketManager.onMessageReceived {
            // Handle and manipulate received message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }
}