package ro.pub.cs.systems.eim.practicaltest02v6

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etServerPort = findViewById<EditText>(R.id.server_port)
        val btnStartServer = findViewById<Button>(R.id.btn_start_server)

        val etClientAddr = findViewById<EditText>(R.id.client_address)
        val etClientPort = findViewById<EditText>(R.id.client_port)
        val etCurrency = findViewById<EditText>(R.id.currency_input) // USD sau EUR
        val btnGetPrice = findViewById<Button>(R.id.btn_get_price)
        val tvResult = findViewById<TextView>(R.id.result_text)

        btnStartServer.setOnClickListener {
            val port = etServerPort.text.toString().toIntOrNull() ?: 2000
            serverThread = ServerThread(port)
            serverThread?.start()
            Toast.makeText(this, "Server Started (10s Cache)", Toast.LENGTH_SHORT).show()
        }

        btnGetPrice.setOnClickListener {
            val addr = etClientAddr.text.toString()
            val port = etClientPort.text.toString().toIntOrNull() ?: 2000
            val currency = etCurrency.text.toString()

            if (currency.isEmpty()) {
                Toast.makeText(this, "Enter USD or EUR", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvResult.text = "Loading..."
            ClientThread(addr, port, currency, tvResult).start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThread?.stopServer()
    }
}