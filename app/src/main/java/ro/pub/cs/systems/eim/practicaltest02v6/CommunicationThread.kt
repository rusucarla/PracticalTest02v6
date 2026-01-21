package ro.pub.cs.systems.eim.practicaltest02v6

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class CommunicationThread(
    private val serverThread: ServerThread,
    private val socket: Socket
) : Thread() {

    override fun run() {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            val currencyRequest = reader.readLine()?.trim()?.uppercase()

            if (!currencyRequest.isNullOrEmpty()) {
                Log.d("BitcoinApp", "[COMM] Client asks for: $currencyRequest")

                // url-ul cu USD https://data-api.coindesk.com/index/cc/v1/latest/tick?market=cadli&instruments=BTC-USD"
                val baseUrl = "https://data-api.coindesk.com/index/cc/v1/latest/tick?market=cadli&instruments="
                val fullUrl = baseUrl + "BTC-$currencyRequest"
                Log.d("BitcoinApp", "[COMM] Full URL: $fullUrl")
                val jsonResponse = serverThread.getBitcoinData(fullUrl)

                var resultToSend = "No data available"

                if (jsonResponse != null) {
                    try {
                        Log.d("BitcoinApp", "[COMM] JSON Response: $jsonResponse")
                        if (jsonResponse.contains(currencyRequest)) {
                            val json = JSONObject(jsonResponse)
                            val data = json.getJSONObject("Data")
                            if (data.has("BTC-$currencyRequest")) {
                                val currencyInfo = data.getJSONObject("BTC-$currencyRequest")
                                val price = currencyInfo.getDouble("VALUE")
                                resultToSend = "Current BTC price in $currencyRequest: $price"
                            }
                            Log.d("BitcoinApp", "[COMM] Found data for $currencyRequest")
                        } else {
                            resultToSend = "Currency $currencyRequest not found in current data."
                        }
                    } catch (e: Exception) {
                        resultToSend = "JSON Error: ${e.message}"
                    }
                }

                writer.println(resultToSend)
            }

            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}