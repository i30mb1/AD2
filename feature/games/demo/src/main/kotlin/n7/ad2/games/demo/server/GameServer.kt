package n7.ad2.games.demo.server

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class GameServer {

    private val scope = CoroutineScope(Job())

    companion object {
        const val host = "192.168.100.8"
        val ports = intArrayOf(50088, 50001)
    }

    fun run() = scope.launch(Dispatchers.IO) {
        try {
            val server = openServer()!!
            val clientSocket = server.accept()!!
            handleClient(clientSocket)
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }

    private fun handleClient(clientSocket: Socket) {
        val inputStream = clientSocket.getInputStream()
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        val headers = readHeaders(bufferedReader)

        val outputStream = clientSocket.getOutputStream()
        val writeer = PrintWriter(outputStream, true)

    }

    private fun readHeaders(reader: BufferedReader) = buildMap {
        var line: String? = reader.readLine()
        while (!line.isNullOrEmpty()) {
            val headerParts = line.split(":")
            if (headerParts.size >= 2) {
                val headerName = headerParts[0].trim()
                val headerValue = headerParts[1].trim()
                put(headerName, headerValue)
            }
            line = reader.readLine()
        }
    }

    private fun openServer(): ServerSocket? {
        for (port in ports) {
            try {
                val id = getIPAddress()
                return ServerSocket(port, 0, InetAddress.getByName(host))
            } catch (e: Exception) {
                println(e)
                // return null
            }
        }
        return null
    }

    private fun getIPAddress(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val adress = inetAddresses.nextElement()
                if (!adress.isLoopbackAddress && !adress.isLinkLocalAddress && adress.isSiteLocalAddress) {
                    return adress.hostAddress
                }
            }
        }
        return null
    }
}

suspend fun main() {
    runBlocking {
        GameServer().run()
    }.join()
}
