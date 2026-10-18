package com.example.network

import com.example.data.model.NetworkMessage
import com.example.data.model.NetworkMessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class LocalMultiplayerServer(
    val port: Int = NetworkUtils.DEFAULT_PORT,
    private val onMessageReceived: (message: NetworkMessage, clientSocketId: String) -> Unit,
    private val onClientConnected: (clientSocketId: String) -> Unit,
    private val onClientDisconnected: (clientSocketId: String) -> Unit,
    private val onError: (error: String) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var acceptJob: Job? = null

    data class ClientConnection(
        val socketId: String,
        val socket: Socket,
        val reader: BufferedReader,
        val writer: PrintWriter,
        var playerId: String? = null
    )

    private val clients = ConcurrentHashMap<String, ClientConnection>()
    var isRunning: Boolean = false
        private set

    fun start() {
        if (isRunning) return
        acceptJob = scope.launch {
            try {
                serverSocket = ServerSocket(port).apply {
                    reuseAddress = true
                }
                isRunning = true

                while (isActive && !serverSocket!!.isClosed) {
                    val socket = serverSocket!!.accept()
                    // Limit max connections to 4 clients
                    if (clients.size >= 4) {
                        try {
                            val pw = PrintWriter(socket.getOutputStream(), true)
                            val errMsg = NetworkMessage(
                                type = NetworkMessageType.ERROR,
                                matchId = "",
                                senderId = "HOST",
                                senderName = "Host Server",
                                payloadJson = "{\"error\":\"Ruangan penuh (Maksimal 4 Pemain)\"}"
                            )
                            pw.println(errMsg.toJsonString())
                            socket.close()
                        } catch (e: Exception) {
                            socket.close()
                        }
                        continue
                    }

                    val socketId = "client_${System.currentTimeMillis()}_${socket.port}"
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val writer = PrintWriter(socket.getOutputStream(), true)

                    val conn = ClientConnection(socketId, socket, reader, writer)
                    clients[socketId] = conn
                    onClientConnected(socketId)

                    // Start read loop for this client
                    launchReadLoop(conn)
                }
            } catch (e: Exception) {
                if (isRunning) {
                    withContext(Dispatchers.Main) {
                        onError("Server socket error: ${e.message}")
                    }
                }
            } finally {
                stop()
            }
        }
    }

    private fun launchReadLoop(conn: ClientConnection) {
        scope.launch {
            try {
                var line: String?
                while (isActive && conn.socket.isConnected && !conn.socket.isClosed) {
                    line = conn.reader.readLine() ?: break
                    if (line.isNotBlank()) {
                        val msg = NetworkMessage.parse(line)
                        if (msg != null) {
                            if (msg.type == NetworkMessageType.PING) {
                                sendToClient(
                                    conn.socketId,
                                    NetworkMessage(
                                        type = NetworkMessageType.PONG,
                                        matchId = msg.matchId,
                                        senderId = "HOST",
                                        senderName = "Host Server"
                                    )
                                )
                            } else {
                                onMessageReceived(msg, conn.socketId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Connection lost or closed
            } finally {
                disconnectClient(conn.socketId)
            }
        }
    }

    fun broadcast(message: NetworkMessage, excludeSocketId: String? = null) {
        val json = message.toJsonString()
        scope.launch {
            clients.forEach { (socketId, conn) ->
                if (socketId != excludeSocketId) {
                    try {
                        conn.writer.println(json)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun sendToClient(socketId: String, message: NetworkMessage) {
        val conn = clients[socketId] ?: return
        val json = message.toJsonString()
        scope.launch {
            try {
                conn.writer.println(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun associatePlayerId(socketId: String, playerId: String) {
        clients[socketId]?.playerId = playerId
    }

    fun getSocketIdForPlayer(playerId: String): String? {
        return clients.entries.firstOrNull { it.value.playerId == playerId }?.key
    }

    private fun disconnectClient(socketId: String) {
        val conn = clients.remove(socketId) ?: return
        try {
            conn.socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onClientDisconnected(socketId)
    }

    fun stop() {
        isRunning = false
        acceptJob?.cancel()
        clients.forEach { (_, conn) ->
            try {
                conn.socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        clients.clear()
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        serverSocket = null
    }
}
