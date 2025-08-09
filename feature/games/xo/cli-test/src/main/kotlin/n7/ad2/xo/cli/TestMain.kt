package n7.ad2.xo.cli

import java.net.InetAddress
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import n7.ad2.xo.cli.controller.CliClientController
import n7.ad2.xo.cli.controller.CliServerController
import n7.ad2.xo.cli.model.ClientStatus
import n7.ad2.xo.cli.model.ServerStatus
import n7.ad2.xo.cli.model.SocketType

suspend fun main() {
    println("🚀 XO Socket Connection Tester - Auto Test Mode")
    println("==============================================")

    // Test all 3 socket types
    for (socketType in SocketType.entries) {
        println("\n" + "=".repeat(50))
        println("Testing ${socketType.name} Socket Type")
        println("=".repeat(50))

        testSocketType(socketType)

        // Wait a bit between tests
        delay(2000)
    }

    println("\n✅ All tests completed!")
}

suspend fun testSocketType(socketType: SocketType) {
    val serverPortDeferred = CompletableDeferred<Int>()

    val serverJob = CoroutineScope(Dispatchers.IO).launch {
        val server = CliServerController.create(socketType)

        try {
            println("📡 Starting ${socketType.name} server...")
            server.start("TestServer", InetAddress.getLoopbackAddress(), 0)

            val serverState = server.state.first { status ->
                status.status !is ServerStatus.Closed
            }

            when (val status = serverState.status) {
                is ServerStatus.Waiting -> {
                    println("✅ ${socketType.name} Server started on ${status.server.ip}:${status.server.port}")
                    serverPortDeferred.complete(status.server.port)
                }

                is ServerStatus.Connected -> {
                    println("✅ ${socketType.name} Server connected on ${status.server.ip}:${status.server.port}")
                    serverPortDeferred.complete(status.server.port)
                }

                else -> {
                    println("❌ ${socketType.name} Server failed to start")
                    serverPortDeferred.completeExceptionally(Exception("Server failed to start"))
                    return@launch
                }
            }

            // Wait for client connection and send a test message
            delay(3000) // Give client time to connect

            // Send test message from server
            server.send("Hello from ${socketType.name} server!")

            // Keep server running for a bit
            delay(5000)

        } catch (e: Exception) {
            println("❌ ${socketType.name} Server error: ${e.message}")
        } finally {
            server.stop()
            println("🛑 ${socketType.name} Server stopped")
        }
    }

    val clientJob = CoroutineScope(Dispatchers.IO).launch {
        val client = CliClientController.create(socketType)

        try {
            // Wait for server to start and get the actual port
            val serverPort = serverPortDeferred.await()
            println("📱 Starting ${socketType.name} client...")
            client.connect("TestClient", InetAddress.getLoopbackAddress(), serverPort)

            val clientState = client.state.first { status ->
                status.status is ClientStatus.Connected
            }

            when (val status = clientState.status) {
                is ClientStatus.Connected -> {
                    println("✅ ${socketType.name} Client connected to ${status.server.ip}:${status.server.port}")
                }

                else -> {
                    println("❌ ${socketType.name} Client failed to connect")
                    return@launch
                }
            }

            // Send test message from client
            client.send("Hello from ${socketType.name} client!")

            // Wait for messages
            delay(3000)

            // Print received messages
            val finalState = client.state.value
            println("📨 ${socketType.name} Client received ${finalState.messages.size} messages:")
            finalState.messages.forEach { message ->
                println("   $message")
            }

        } catch (e: Exception) {
            println("❌ ${socketType.name} Client error: ${e.message}")
        } finally {
            client.disconnect()
            println("🛑 ${socketType.name} Client disconnected")
        }
    }

    // Wait for both to complete
    joinAll(serverJob, clientJob)
}

