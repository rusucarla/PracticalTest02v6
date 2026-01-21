package ro.pub.cs.systems.eim.practicaltest02v6

import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: Int,
    private val currency: String,
    private val outputView: TextView
) : Thread() {

    override fun run() {
        try {
            val socket = Socket(address, port)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.println(currency)

            val response = reader.readLine()

            outputView.post {
                outputView.text = response
            }

            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
            outputView.post {
                outputView.text = "Error: ${e.message}"
            }
        }
    }
}