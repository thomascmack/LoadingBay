package ca.unb.mobiledev.appdevproject

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    private val inv = mapOf(1000 to "square", 1001 to "circle", 1002 to "triangle")
    private val scannedItems = ItemList()

    private lateinit var itemName : TextView
    private lateinit var itemID : TextView
    private lateinit var itemQuantity : TextView
    private lateinit var scanButton : Button
    private lateinit var undoButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scanButton = findViewById(R.id.scanButton)
        itemName = findViewById(R.id.itemName)
        itemID = findViewById(R.id.itemID)
        itemQuantity = findViewById(R.id.quantity)
        undoButton = findViewById(R.id.undoButton)

        undoButton.setOnClickListener {
            scannedItems.pop()
            updateTextView()
        }

        scanButton.setOnClickListener {
            scanQRCode(this)
        }
    }

    fun scanQRCode(context : Context) {
        //configure options for qrcode scanner
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        //instantiate scanner
        val scanner = GmsBarcodeScanning.getClient(context, options)

        //start scan and handle results
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue: String? = barcode.rawValue
                val id = rawValue?.toInt() ?: 0
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, id.toString(), duration)
                toast.show()

                if(inv.containsKey(id)) {
                    scannedItems.push(id, inv.getValue(id))
                }
                updateTextView()
            }
            .addOnCanceledListener {
                val text = "Canceled"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
            .addOnFailureListener { e ->
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, e.toString(), duration)
                toast.show()
            }

    }

    fun updateTextView() {
        if(scannedItems.isNotEmpty()) {
            itemName.text = scannedItems.last().name
            itemID.text = scannedItems.last().id.toString()
            itemQuantity.text = "x" + scannedItems.last().quantity
        }
        else {
            itemName.text = "Please scan an item"
            itemID.text = null
            itemQuantity.text = null
        }
    }
}