package ca.unb.mobiledev.appdevproject.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.classes.ProductList
import ca.unb.mobiledev.appdevproject.ui.MyViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class ItemScanActivity : ComponentActivity() {

    private lateinit var startView : View
    private lateinit var scannedView : View
    private lateinit var noItemView : View
    private lateinit var itemName : TextView
    private lateinit var itemID : TextView
    private lateinit var damaged : CheckBox
    private lateinit var scanButton : Button
    private lateinit var scanManualButton : Button
    private lateinit var undoButton : Button
    private lateinit var viewFullList : Button
    private lateinit var descExitText : EditText
    private lateinit var scanner : GmsBarcodeScanner
    private lateinit var viewModel : MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_scan)

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
        scanManualButton = findViewById(R.id.manualButton)
        undoButton = findViewById(R.id.undoButton)
        viewFullList = findViewById(R.id.fullList)

        descExitText = findViewById(R.id.description)

        scanButton.setOnClickListener {
            scanQRCode(this)
        }

        scanManualButton.setOnClickListener {
            val dialog = Dialog(this, R.style.DialogWindowTheme)
            dialog.setContentView(R.layout.enter_product_id_dialog)
            dialog.show()
            val finishProductIDButton : Button = dialog.findViewById(R.id.finishButton)
            val cancelProductIDButton : Button = dialog.findViewById(R.id.cancelButton)


            finishProductIDButton.setOnClickListener {
                val upc =  dialog.findViewById<EditText>(R.id.productCode).text.toString()
                if(upc.isNotEmpty()) {
                    viewModel.search(upc.toLong())
                }
                else Toast.makeText(this, "Please enter a product code", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            cancelProductIDButton.setOnClickListener {
                dialog.cancel()
            }
        }

        viewFullList.setOnClickListener {
            val intent = Intent(this@ItemScanActivity, ProductListActivity::class.java)
            startActivity(intent)
        }

        damaged.tag = true
        damaged.setOnCheckedChangeListener { buttonView, isChecked ->
            if(damaged.tag == true) {
                manifest.setDamage(manifest.top())
            }
        }

        Log.i("main", "loading view model")
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        viewModel.searchItems.observe(this) { products ->
            products?.let {
                if(products.isNotEmpty()) {
                    for (p in products) {
                        manifest.scanItem(p.upc, p.itemName)
                    }
                    switchViewTo(scannedView)
                }
                else {
                    switchViewTo(noItemView)
                }
            }
        }

        undoButton.setOnClickListener {
            manifest.undo()
            if(manifest.isNotEmpty()) {
                switchViewTo(scannedView)
            }
            else {
                switchViewTo(startView)
            }
        }

        descExitText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                manifest.setDescription(manifest.top() ,s.toString()
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("ItemScan", "activity re-entered")
        if(manifest.isNotEmpty()) {
            switchViewTo(scannedView)
        }
        else {
            switchViewTo(startView)
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

                viewModel.search(id)
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
                damaged.tag = false
                damaged.isChecked = manifest.top()?.damaged ?: false
                damaged.tag = true
                descExitText.setText(manifest.top()?.description)
                itemName.text = manifest.getItemName(manifest.top())
                itemID.text = manifest.top()?.upc.toString()
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
        private val manifest : ProductList = ManifestScanActivity.getManifest()
        fun getScannedItems(): ProductList {return manifest}
    }
}