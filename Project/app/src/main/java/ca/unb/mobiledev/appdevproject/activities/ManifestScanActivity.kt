package ca.unb.mobiledev.appdevproject.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.classes.ProductList
import ca.unb.mobiledev.appdevproject.ui.MyViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


class ManifestScanActivity : ComponentActivity() {

    private lateinit var scanner : GmsBarcodeScanner
    private lateinit var scanButton : Button
    private lateinit var scanManualButton : Button
    private lateinit var viewModel : MyViewModel
    private lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manifest_scan)

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        scanner = GmsBarcodeScanning.getClient(this, options)

        scanButton = findViewById(R.id.QRscanButton)
        scanManualButton = findViewById(R.id.manualButton)


        scanButton.setOnClickListener {
            scanQRCode(this)
        }

        dialog = Dialog(this, R.style.DialogWindowTheme)

        scanManualButton.setOnClickListener {
            //dialog = Dialog(this, R.style.DialogWindowTheme)
            dialog.setContentView(R.layout.enter_shippment_id_dialog)
            val idEditText = dialog.findViewById<EditText>(R.id.shipmentID)
            dialog.window!!
                .clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialog.window!!
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
            idEditText.requestFocus()
            val finishProductIDButton : Button = dialog.findViewById(R.id.confirmButton)
            val cancelProductIDButton : Button = dialog.findViewById(R.id.cancelButton)

            idEditText.setOnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val id = idEditText.text.toString()
                    if (id.isNotEmpty()) {
                        viewModel.findManifest(id.toLong())
                    }
                    handled = true
                }
                handled
            }

            finishProductIDButton.setOnClickListener {
                val id =  idEditText.text.toString()
                if(id.isNotEmpty()) {
                    viewModel.findManifest(id.toLong())
                }
            }

            cancelProductIDButton.setOnClickListener {
                dialog.cancel()
            }
        }

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        viewModel.manifestSearch.observe(this) { pWi ->
            pWi?.let {
                if(pWi.isNotEmpty()) {
                    dialog.dismiss()
                    manifest = ProductList(pWi[0].items[0].shipmentID, 0)
                    for(p in pWi) {
                        Log.d("Shipment", p.toString())
                        manifest.add(p)
                        for(i in p.items) {
                            if(i.itemID > manifest.maxItemID) manifest.maxItemID = i.itemID
                        }
                    }
                    val intent = Intent(this@ManifestScanActivity, ItemScanActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this, "Please enter a valid shipment ID", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun scanQRCode(context : Context) {
        //start scan and handle results
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue: String? = barcode.rawValue
                val id = rawValue?.toLong() ?: 0
                viewModel.findManifest(id)
            }
            .addOnFailureListener { e ->
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(context, e.toString(), duration)
                toast.show()
            }
    }

    companion object {
        private lateinit var manifest : ProductList
        fun getManifest(): ProductList {return manifest}
    }
}