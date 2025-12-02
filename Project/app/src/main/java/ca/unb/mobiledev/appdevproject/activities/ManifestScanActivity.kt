package ca.unb.mobiledev.appdevproject.activities

import android.Manifest
import android.app.ComponentCaller
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.classes.ProductList
import ca.unb.mobiledev.appdevproject.ui.MyViewModel

class ManifestScanActivity : ComponentActivity() {
    private lateinit var scanButton : Button
    private lateinit var scanManualButton : Button
    private lateinit var viewModel : MyViewModel
    private lateinit var dialog : Dialog
    private val CAMERA_PERMISSION: Array<String> = arrayOf(Manifest.permission.CAMERA)
    private val CAMERA_REQUEST_CODE: Int = 10
    private val QR_SCAN_REQUEST = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manifest_scan)

        scanButton = findViewById(R.id.QRscanButton)
        scanManualButton = findViewById(R.id.manualButton)


        scanButton.setOnClickListener {
            if (hasCameraPermission()) {
                startScan()
            } else {
                requestPermission()
            }
        }

        dialog = Dialog(this, R.style.DialogWindowTheme)

        scanManualButton.setOnClickListener {
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
                            if(i.shipmentID != manifest.shipmentID) {
                                p.items.remove(i)
                                break
                            }
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
        startActivityForResult(intent, QR_SCAN_REQUEST)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if (requestCode == QR_SCAN_REQUEST && resultCode == RESULT_OK && data != null) {
            val shipmentID = data.getLongExtra("value",  0)
            viewModel.findManifest(shipmentID)
        }
    }

    companion object {
        private lateinit var manifest : ProductList
        fun getManifest(): ProductList {return manifest}
    }
}