package ca.unb.mobiledev.appdevproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
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

    private lateinit var itemLayout : RelativeLayout
    private lateinit var itemName : TextView
    private lateinit var itemID : TextView
    private lateinit var scanButton : Button
    private lateinit var undoButton : Button
    private lateinit var damaged : CheckBox

    private lateinit var viewFullList : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        itemLayout = findViewById(R.id.item_view)
        itemLayout.visibility = RelativeLayout.INVISIBLE
        scanButton = findViewById(R.id.scanButton)
        itemName = findViewById(R.id.itemName)
        itemID = findViewById(R.id.itemID)
        //itemQuantity = findViewById(R.id.quantity)
        undoButton = findViewById(R.id.undoButton)
        damaged = findViewById(R.id.damaged)
        viewFullList = findViewById(R.id.fullList)

        undoButton.setOnClickListener {
            scannedItems.pop()
            updateTextView()
        }

        scanButton.setOnClickListener {
            scanQRCode(this)
        }

        viewFullList.setOnClickListener {
            val intent = Intent(this@MainActivity, ScannedList::class.java)
            startActivity(intent)
        }

        damaged.tag = true
        damaged.setOnCheckedChangeListener { buttonView, isChecked ->
            if(scannedItems.isNotEmpty() && damaged.tag == true) {
                scannedItems.setDamage(scannedItems.top())
            }
        }
    }

    fun scanQRCode(context : Context) {
        //configure options for qrcode scanner
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
            .build()

        //instantiate scanner
        val scanner = GmsBarcodeScanning.getClient(context, options)

        //start scan and handle results
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue: String? = barcode.rawValue
                val id = rawValue?.toLong() ?: 0
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, id.toString(), duration)
                toast.show()

                if(inv.containsKey(id)) {
                    scannedItems.push(id, inv.getValue(id))
                    updateTextView()
                }
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
            damaged.tag = false
            damaged.isChecked = scannedItems.top().damaged
            damaged.tag = true
            val i = scannedItems.top()
            itemName.text = i.name
            itemID.text = i.id.toString()
            itemLayout.visibility = RelativeLayout.VISIBLE
        }
        else {
            itemLayout.visibility = RelativeLayout.INVISIBLE
        }
    }

    companion object {
        private val scannedItems = ItemList()
        private val inv = mapOf(777499239876 to "Neil Young - Decade",
                                75678124020 to "Phil Collins - No Jacket Required",
                                606949304522 to "Weezer - Green Album")

        fun getScannedItems(): ItemList {return scannedItems}
    }
}