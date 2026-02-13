package n7.ad2.xo.cli

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.xo.cli.controller.CliClientController
import n7.ad2.xo.cli.controller.CliServerController
import n7.ad2.xo.cli.model.SocketType
import java.net.InetAddress

fun main() = runBlocking {
    println("🚀 XO Socket Connection Tester")
    println("==============================")

    while (true) {
        println("\nSelect mode:")
        println("1. Server mode")
        println("2. Client mode")
        println("3. Exit")
        print("Your choice: ")

        when (readLine()?.trim()) {
            "1" -> runServerMode()
            "2" -> runClientMode()
            "3" -> {
                println("Goodbye!")
                break
            }

            else -> println("Invalid choice, please try again.")
        }
    }
}

suspend fun runServerMode() {
    println("\n📡 Server Mode")
    println("================")

    val socketType = selectSocketType()
    val serverName = getServerName()

    val server = CliServerController.create(socketType)

    println("Starting server...")
    try {
        server.start(serverName, InetAddress.getLoopbackAddress(), 0)

        val serverState = server.state.first { it.status !is n7.ad2.xo.cli.model.ServerStatus.Closed }
        when (val status = serverState.status) {
            is n7.ad2.xo.cli.model.ServerStatus.Waiting -> {
                println("✅ Server started on ${status.server.ip}:${status.server.port}")
                println("Waiting for connections...")
            }

            is n7.ad2.xo.cli.model.ServerStatus.Connected -> {
                println("🔗 Client connected!")
            }

            else -> {}
        }

        // Запускаем обработку сообщений
        runServerMessageLoop(server)
    } catch (e: Exception) {
        println("❌ Server error: ${e.message}")
    } finally {
        server.stop()
        println("Server stopped.")
    }
}

suspend fun runClientMode() {
    println("\n📱 Client Mode")
    println("================")

    val socketType = selectSocketType()
    val serverInfo = getServerInfo()

    val client = CliClientController.create(socketType)

    println("Connecting to server...")
    try {
        client.connect(serverInfo.name, InetAddress.getByName(serverInfo.host), serverInfo.port)

        val clientState = client.state.first { it.status !is n7.ad2.xo.cli.model.ClientStatus.Disconnected }
        when (clientState.status) {
            is n7.ad2.xo.cli.model.ClientStatus.Connected -> {
                println("✅ Connected to ${serverInfo.host}:${serverInfo.port}")
            }

            else -> {}
        }

        // Запускаем обработку сообщений
        runClientMessageLoop(client)
    } catch (e: Exception) {
        println("❌ Connection error: ${e.message}")
    } finally {
        client.disconnect()
        println("Disconnected.")
    }
}

suspend fun runServerMessageLoop(server: CliServerController) {
    println("\n💬 Server ready to send/receive messages")
    println("Type messages to send, or 'quit' to stop")

    while (true) {
        print("> ")
        val input = readLine()?.trim() ?: break

        when {
            input.equals("quit", ignoreCase = true) -> break
            input.isNotEmpty() -> {
                server.send(input)
                println("✅ Message sent")
            }
        }

        // Небольшая задержка для обработки входящих сообщений
        delay(100)
    }
}

suspend fun runClientMessageLoop(client: CliClientController) {
    println("\n💬 Client ready to send/receive messages")
    println("Type messages to send, or 'quit' to disconnect")

    while (true) {
        print("> ")
        val input = readLine()?.trim() ?: break

        when {
            input.equals("quit", ignoreCase = true) -> break
            input.isNotEmpty() -> {
                client.send(input)
                println("✅ Message sent")
            }
        }

        // Небольшая задержка для обработки входящих сообщений
        delay(100)
    }
}

fun selectSocketType(): SocketType {
    println("\nSelect socket type:")
    println("1. RAW Socket")
    println("2. HTTP")
    println("3. WebSocket")
    print("Your choice: ")

    return when (readLine()?.trim()) {
        "1" -> SocketType.RAW
        "2" -> SocketType.HTTP
        "3" -> SocketType.WEBSOCKET
        else -> {
            println("Invalid choice, defaulting to RAW Socket")
            SocketType.RAW
        }
    }
}

fun getServerName(): String {
    print("Enter server name (default: 'TestServer'): ")
    return readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: "TestServer"
}

data class ServerInfo(val name: String, val host: String, val port: Int)

fun getServerInfo(): ServerInfo {
    print("Enter server host (default: 'localhost'): ")
    val host = readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: "localhost"

    print("Enter server port: ")
    val port = readLine()?.trim()?.toIntOrNull() ?: run {
        println("Invalid port, using default 8080")
        8080
    }

    print("Enter server name (default: 'RemoteServer'): ")
    val name = readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: "RemoteServer"

    return ServerInfo(name, host, port)
}
