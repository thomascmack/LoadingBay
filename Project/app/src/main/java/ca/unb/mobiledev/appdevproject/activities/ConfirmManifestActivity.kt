package ca.unb.mobiledev.appdevproject.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.adapters.ProductListAdapter

class ConfirmManifestActivity : AppCompatActivity() {
    lateinit var productRecyclerView: RecyclerView
    lateinit var backButton: Button
    lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.confirm_manifest_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scannedList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        productRecyclerView = findViewById(R.id.productRecyclerView)

        val scannedItems = ItemScanActivity.getScannedItems()

        productRecyclerView.adapter = ProductListAdapter(scannedItems)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val intent = Intent(this@ConfirmManifestActivity, ManifestScanActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}