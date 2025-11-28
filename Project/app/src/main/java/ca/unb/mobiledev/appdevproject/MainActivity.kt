package ca.unb.mobiledev.appdevproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.appdevproject.ui.ProductViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {

    private lateinit var startView : View
    private lateinit var scannedView : View
    private lateinit var noItemView : View
    private lateinit var itemName : TextView
    private lateinit var itemID : TextView
    private lateinit var damaged : CheckBox
    private lateinit var scanButton : Button
    private lateinit var undoButton : Button
    private lateinit var viewFullList : Button
    private lateinit var scanner : GmsBarcodeScanner
    private lateinit var productViewModel : ProductViewModel

    private var shipmentID : Long = 1
    //private var manifest

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("main", "hello")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //configure options for qrcode scanner
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
            .build()

        //instantiate scanner
        scanner = GmsBarcodeScanning.getClient(this, options)

        startView = findViewById(R.id.start)
        scannedView = findViewById(R.id.scannedItem)
        noItemView = findViewById(R.id.invalidItem)

        itemName = findViewById(R.id.itemName)
        itemID = findViewById(R.id.itemID)
        damaged = findViewById(R.id.damaged)

        scanButton = findViewById(R.id.scanButton)
        undoButton = findViewById(R.id.undoButton)
        viewFullList = findViewById(R.id.fullList)

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

        Log.i("main", "loading view model")
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        productViewModel.searchItems.observe(this) { products ->
            products?.let {
                for(p in products) {
                    scannedItems.push(p.upc)
                    damaged.tag = false
                    damaged.isChecked = scannedItems.top().damaged
                    damaged.tag = true
                    itemName.text = p.itemName
                    itemID.text = p.upc.toString()
                }
                switchViewTo(scannedView)
                return@observe
            }
            switchViewTo(noItemView)
        }

        undoButton.setOnClickListener {
            scannedItems.pop()
            if(scannedItems.isNotEmpty()) {
                productViewModel.search(scannedItems.top().upc)
                /*search will push found item onto scannedItem stack
                (intended behaviour when search is called from scanQRCode function)
                don't want this to happen when undoing, so just pop added item off stack
                TODO (maybe but probably not) find better solution for this
                 */
                scannedItems.pop()
            }
            else {
                switchViewTo(startView)
            }
        }
    }

    fun scanQRCode(context : Context) {
        //start scan and handle results
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue: String? = barcode.rawValue
                val id = rawValue?.toLong() ?: 0
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, id.toString(), duration)
                toast.show()

                productViewModel.search(id)
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

    fun switchViewTo(view : View) {
        when (view) {
            startView -> {
                startView.visibility = View.VISIBLE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.GONE
            }
            scannedView -> {
                startView.visibility = View.GONE
                scannedView.visibility = View.VISIBLE
                noItemView.visibility = View.GONE
            }
            noItemView -> {
                startView.visibility = View.GONE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private val scannedItems = ItemList()
        fun getScannedItems(): ItemList {return scannedItems}
    }
}