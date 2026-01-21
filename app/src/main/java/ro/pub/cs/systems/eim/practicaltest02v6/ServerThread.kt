package ro.pub.cs.systems.eim.practicaltest02v6

import android.util.Log
import java.net.ServerSocket
import java.net.Socket

class ServerThread(private val port: Int) : Thread() {

    private var serverSocket: ServerSocket? = null

    private val BITCOIN_URL = "https://data-api.coindesk.com/index/cc/v1/latest/tick?market=cadli&instruments=BTC-USD"

    private var cacheData: String? = null
    private var lastUpdateTimestamp: Long = 0L
    private val EXPIRATION_TIME_MS = 10000L // 10 secunde

    override fun run() {
        try {
            serverSocket = ServerSocket(port)
            Log.d("BitcoinApp", "[SERVER] Started on port $port")

            while (!interrupted()) {
                val socket = serverSocket?.accept()
                if (socket != null) {
                    CommunicationThread(this, socket).start()
                }
            }
        } catch (e: Exception) {
            Log.e("BitcoinApp", "[SERVER] Error: ${e.message}")
        }
    }

    @Synchronized
    fun getBitcoinData(
        url: String
    ): String? {
        val currentTime = System.currentTimeMillis()
        val age = currentTime - lastUpdateTimestamp

        if (cacheData != null && age < EXPIRATION_TIME_MS) {
            Log.d("BitcoinApp", "[CACHE] Data found in cache ${age}ms (<10s)")
            return cacheData
        } else {
            Log.d("BitcoinApp", "[CACHE] Data expired or missing ${age}ms")

            val newData = HttpUtil.fetchUrl(url)

            if (newData != null) {
                cacheData = newData
                lastUpdateTimestamp = System.currentTimeMillis()
                Log.d("BitcoinApp", "[CACHE] New cache $lastUpdateTimestamp")
            }
            return cacheData
        }
    }

    fun stopServer() {
        try { serverSocket?.close() } catch (e: Exception) {}
    }
}