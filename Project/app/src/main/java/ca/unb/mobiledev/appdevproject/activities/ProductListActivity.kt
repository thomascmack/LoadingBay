package ca.unb.mobiledev.appdevproject.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.adapters.ProductListAdapter
import ca.unb.mobiledev.appdevproject.R

class ProductListActivity : AppCompatActivity() {
    private lateinit var productRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scanned_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scannedList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        productRecyclerView = findViewById(R.id.productRecyclerView)

        val scannedItems = ItemScanActivity.getScannedItems()

        productRecyclerView.adapter = ProductListAdapter(scannedItems)
    }
}