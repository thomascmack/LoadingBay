package ca.unb.mobiledev.appdevproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.adapters.ProductListAdapter
import ca.unb.mobiledev.appdevproject.classes.ProductList

class ConfirmManifestActivity : AppCompatActivity() {
    lateinit var productRecyclerView: RecyclerView
    lateinit var backButton: Button
    lateinit var saveButton: Button
    lateinit var damaged: TextView
    lateinit var missing: TextView
    lateinit var extra: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.confirm_manifest_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scannedList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        damaged = findViewById(R.id.damaged_items)
        missing = findViewById(R.id.missing_items)
        extra = findViewById(R.id.extra_items)

        calculateTotals()

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

    fun calculateTotals() {
        val totalDamaged = manifest.totalDamaged()
        val totalMissing = manifest.totalMissing()
        val totalExtra = manifest.totalExtra()

        if(totalDamaged > 0){
            damaged.visibility = View.VISIBLE
            damaged.text = getString(R.string.count_damaged, totalDamaged)
        }
        else damaged.visibility = View.GONE
        if(totalMissing > 0){
            missing.visibility = View.VISIBLE
            missing.text = getString(R.string.missing_items, totalMissing)
        }
        else missing.visibility = View.GONE
        if(totalExtra > 0){
            extra.visibility = View.VISIBLE
            extra.text = getString(R.string.extra_items, totalExtra)
        }
        else extra.visibility = View.GONE
    }
    companion object {
        private val manifest : ProductList = ManifestScanActivity.getManifest()
        fun getScannedItems(): ProductList {return manifest}
    }
}