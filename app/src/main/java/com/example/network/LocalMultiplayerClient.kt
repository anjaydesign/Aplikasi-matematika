package com.example.network

import com.example.data.model.NetworkMessage
import com.example.data.model.NetworkMessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class LocalMultiplayerClient(
    private val onMessageReceived: (message: NetworkMessage) -> Unit,
    private val onConnected: () -> Unit,
    private val onDisconnected: (reason: String) -> Unit,
    private val onError: (error: String) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var readJob: Job? = null
    private var pingJob: Job? = null

    var isConnected: Boolean = false
        private set

    fun connect(hostIp: String, port: Int = NetworkUtils.DEFAULT_PORT) {
        disconnect()
        scope.launch {
            try {
                val newSocket = Socket()
                newSocket.connect(InetSocketAddress(hostIp, port), 5000)
                socket = newSocket
                writer = PrintWriter(newSocket.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(newSocket.getInputStream()))
                isConnected = true

                withContext(Dispatchers.Main) {
                    onConnected()
                }

                // Start read loop
                startReadLoop()
                // Start heartbeat
                startPingLoop()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Gagal terhubung ke Host ($hostIp:$port): ${e.localizedMessage ?: "Timeout"}")
                }
                disconnect("Koneksi gagal")
            }
        }
    }

    private fun startReadLoop() {
        readJob = scope.launch {
            try {
                val r = reader ?: return@launch
                var line: String?
                while (isActive && isConnected) {
                    line = r.readLine() ?: break
                    if (line.isNotBlank()) {
                        val msg = NetworkMessage.parse(line)
                        if (msg != null) {
                            if (msg.type != NetworkMessageType.PONG) {
                                withContext(Dispatchers.Main) {
                                    onMessageReceived(msg)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Connection broken
            } finally {
                disconnect("Terputus dari server host")
            }
        }
    }

    private fun startPingLoop() {
        pingJob = scope.launch {
            while (isActive && isConnected) {
                delay(8000)
                sendMessage(
                    NetworkMessage(
                        type = NetworkMessageType.PING,
                        matchId = "",
                        senderId = "CLIENT",
                        senderName = "Client"
                    )
                )
            }
        }
    }

    fun sendMessage(message: NetworkMessage) {
        val json = message.toJsonString()
        scope.launch {
            try {
                writer?.println(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect(reason: String = "Keluar dari ruangan") {
        if (!isConnected && socket == null) return
        isConnected = false
        readJob?.cancel()
        pingJob?.cancel()
        try {
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        socket = null
        writer = null
        reader = null
        scope.launch(Dispatchers.Main) {
            onDisconnected(reason)
        }
    }
}
