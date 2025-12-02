package ca.unb.mobiledev.appdevproject.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.adapters.ProductListAdapter
import ca.unb.mobiledev.appdevproject.classes.ProductList
import ca.unb.mobiledev.appdevproject.ui.MyViewModel
import com.google.mlkit.vision.barcode.common.Barcode

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
    //private lateinit var scanner : GmsBarcodeScanner
    private lateinit var viewModel : MyViewModel
    private lateinit var dialog : Dialog
    private lateinit var productRecyclerView: RecyclerView
    private var showList : Boolean = false
    private val EDIT_REQUEST_CODE = 1
    private val CAMERA_PERMISSION: Array<String> = arrayOf(Manifest.permission.CAMERA)
    private val CAMERA_REQUEST_CODE: Int = 10
    private val UPC_SCAN_REQUEST = 2

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
//        val options = GmsBarcodeScannerOptions.Builder()
//            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
//            .build()
//
//        //instantiate scanner
//        scanner = GmsBarcodeScanning.getClient(this, options)

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
            if (hasCameraPermission()) {
                startScan()
            } else {
                requestPermission()
            }
        }

        finishButton = findViewById(R.id.finishButton)
        finishButton.setOnClickListener {
            val intent = Intent(this@ItemScanActivity, ConfirmManifestActivity::class.java)
            startActivity(intent)
        }

        dialog = Dialog(this, R.style.DialogWindowTheme)

        // Manual button click listener
        scanManualButton = findViewById(R.id.manualButton)
        scanManualButton.setOnClickListener {
            dialog.setContentView(R.layout.enter_product_id_dialog)
            dialog.window!!
                .clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window!!
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
            val finishProductIDButton : Button = dialog.findViewById(R.id.finishButton)
            val cancelProductIDButton : Button = dialog.findViewById(R.id.cancelButton)
            val upcEditText = dialog.findViewById<EditText>(R.id.productCode)
            upcEditText.requestFocus()

            upcEditText.setOnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val upc = upcEditText.text.toString()
                    if (upc.isNotEmpty()) {
                        viewModel.search(upc.toLong())
                    }
                    handled = true
                }
                handled
            }

            finishProductIDButton.setOnClickListener {
                val upc =  upcEditText.text.toString()
                if(upc.isNotEmpty()) {
                    viewModel.search(upc.toLong())
                }
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
            showList = !showList
            if(showList) {
                switchViewTo(productRecyclerView)
            }
            else {
                if (noItemView.isVisible && manifest.isNotEmpty()) {
                    switchViewTo(scannedView)
                    return@setOnClickListener
                }
                if (manifest.isNotEmpty()) {
                    switchViewTo(scannedView)
                } else {
                    switchViewTo(startView)
                }
            }
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
                    dialog.dismiss()
                    for (p in products) {
                        Log.d("Extra", p.itemName)
                        manifest.scanItem(p.upc, p.itemName)
                    }
                    switchViewTo(scannedView)
                }
                else {
                    dialog.dismiss()
                    Log.d("Items","found no item")
                    switchViewTo(noItemView)
                }
            }
        }

        undoButton.setOnClickListener {
            if(showList) {
                switchViewTo(productRecyclerView)
            }
            else {
                if (noItemView.isVisible && manifest.isNotEmpty()) {
                    switchViewTo(scannedView)
                    return@setOnClickListener
                }
                manifest.undo()
                if (manifest.isNotEmpty()) {
                    switchViewTo(scannedView)
                } else {
                    switchViewTo(startView)
                }
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

        productRecyclerView = findViewById(R.id.scannedList)
        productRecyclerView.adapter = ProductListAdapter(manifest)
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


    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            CAMERA_PERMISSION,
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startScan()
            } else {
                Toast.makeText(this, "Please grant camera permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScan() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, UPC_SCAN_REQUEST)
    }

    override fun onResume() {
        super.onResume()
        if(!showList) {
            if (manifest.scanStack.isEmpty()) {
                switchViewTo(startView)
            } else {
                updateScannedView()
            }
        }
        else {
            switchViewTo(productRecyclerView)
        }
    }

//    fun scanQRCode(context : Context) {
//        //start scan and handle results
//        scanner.startScan()
//            .addOnSuccessListener { barcode ->
//                val rawValue: String? = barcode.rawValue
//                val id = rawValue?.toLong() ?: 0
//                Log.d("UPC", "$id")
//                viewModel.search(id)
//            }
//            .addOnFailureListener { e ->
//                val duration = Toast.LENGTH_SHORT
//
//                val toast = Toast.makeText(context, e.toString(), duration)
//                toast.show()
//            }
//    }

    fun switchViewTo(view : View) {
        when (view) {
            startView -> {
                if(noItemView.isVisible) return
                startView.visibility = View.VISIBLE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.GONE
                productRecyclerView.visibility = View.GONE
            }
            scannedView -> {
                updateScannedView()
                startView.visibility = View.GONE
                scannedView.visibility = View.VISIBLE
                noItemView.visibility = View.GONE
                productRecyclerView.visibility = View.GONE
            }
            noItemView -> {
                Log.d("Items", "no item view")
                startView.visibility = View.GONE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.VISIBLE
                productRecyclerView.visibility = View.GONE
            }
            productRecyclerView -> {
                startView.visibility = View.GONE
                scannedView.visibility = View.GONE
                noItemView.visibility = View.GONE
                productRecyclerView.visibility = View.VISIBLE
                productRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    fun updateScannedView() {
        damaged.tag = false
        damaged.isChecked = manifest.top()?.damaged ?: false
        damaged.tag = true
        descExitText.setText(manifest.top()?.description)
        itemName.text = manifest.getItemName(manifest.top())
        itemID.text = getString(R.string.item_id, manifest.top()?.upc)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val itemID = data.getLongExtra("itemID", 0)
            val upc = data.getLongExtra("upc", 0)
            val damaged = data.getBooleanExtra("damaged", false)
            Log.d("Item Edit", "$damaged")
            val description = data.getStringExtra("description")
            manifest.getItem(upc, itemID)?.damaged = damaged
            manifest.getItem(upc, itemID)?.description = description
            productRecyclerView.adapter?.notifyDataSetChanged()
        }
        else if(requestCode == UPC_SCAN_REQUEST && resultCode == RESULT_OK && data != null) {
            val upc = data.getLongExtra("value", 0)
            viewModel.search(upc)
        }
    }

    companion object {
        private lateinit var manifest : ProductList
        fun getScannedItems(): ProductList {return manifest}
    }
}