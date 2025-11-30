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
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
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
import androidx.core.view.isVisible

class ItemScanActivity : ComponentActivity() {

    private lateinit var startView : View
    private lateinit var scannedView : View
    private lateinit var noItemView : View
    private lateinit var itemName : TextView
    private lateinit var itemID : TextView
    private lateinit var damaged : CheckBox
    private lateinit var scanButton : Button
    private lateinit var scanManualButton : Button
    private lateinit var closeButton : Button
    private lateinit var undoButton : Button
    private lateinit var finishButton : Button
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

        manifest = ManifestScanActivity.getManifest()

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

        undoButton = findViewById(R.id.undoButton)
        viewFullList = findViewById(R.id.fullList)

        descExitText = findViewById(R.id.description)

        // Scan button click listener
        scanButton = findViewById(R.id.scanButton)
        scanButton.setOnClickListener {
            scanQRCode(this)
        }

        finishButton = findViewById(R.id.finishButton)
        finishButton.setOnClickListener {
            val intent = Intent(this@ItemScanActivity, ConfirmManifestActivity::class.java)
            startActivity(intent)
        }

        // Manual button click listener
        scanManualButton = findViewById(R.id.manualButton)
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

        //Send Back Swipe To Close Button Dialog
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                createCloseDialog()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        //Cancel button click listener
        closeButton = findViewById(R.id.cancelButton)
        closeButton.setOnClickListener {
            createCloseDialog()
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
                    Log.d("Items","found no item")
                    switchViewTo(noItemView)
                }
            }
        }

        undoButton.setOnClickListener {
            if(noItemView.isVisible && manifest.isNotEmpty()) {
                switchViewTo(scannedView)
                return@setOnClickListener
            }
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

    fun createCloseDialog() {
        val dialog = Dialog(this, R.style.DialogWindowTheme)
        dialog.setContentView(R.layout.end_shipment_scan_dialog)
        dialog.show()
        val confirmButton: Button = dialog.findViewById(R.id.confirmButton)
        val backButton: Button = dialog.findViewById(R.id.backButton)

        confirmButton.setOnClickListener {
            dialog.cancel()
            finish()
        }

        backButton.setOnClickListener {
            dialog.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ItemScan", "activity re-entered")
        if(manifest.scanStack.isEmpty()) {
            switchViewTo(startView)
        }
        else {
            updateScannedView()
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
                if(noItemView.isVisible) return
                startView.visibility = View.VISIBLE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.GONE
            }
            scannedView -> {
                updateScannedView()
                startView.visibility = View.GONE
                scannedView.visibility = View.VISIBLE
                noItemView.visibility = View.GONE
            }
            noItemView -> {
                Log.d("Items", "no item view")
                startView.visibility = View.GONE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.VISIBLE
            }
        }
    }

    fun updateScannedView() {
        damaged.tag = false
        damaged.isChecked = manifest.top()?.damaged ?: false
        damaged.tag = true
        descExitText.setText(manifest.top()?.description)
        itemName.text = manifest.getItemName(manifest.top())
        itemID.text = manifest.top()?.upc.toString()
    }

    companion object {
        private lateinit var manifest : ProductList
        fun getScannedItems(): ProductList {return manifest}
    }
}