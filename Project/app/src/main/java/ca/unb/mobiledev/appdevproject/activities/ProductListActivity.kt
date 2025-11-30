package ca.unb.mobiledev.appdevproject.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.appdevproject.R
import ca.unb.mobiledev.appdevproject.adapters.ProductListAdapter
import ca.unb.mobiledev.appdevproject.classes.ProductList


class ProductListActivity : AppCompatActivity() {
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var scannedItems : ProductList

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

        scannedItems = ItemScanActivity.getScannedItems()

        productRecyclerView.adapter = ProductListAdapter(scannedItems)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val itemID = data.getLongExtra("itemID", 0)
            val upc = data.getLongExtra("upc", 0)
            val damaged = data.getBooleanExtra("damaged", false)
            Log.d("Item Edit", "$damaged")
            val description = data.getStringExtra("description")
            scannedItems.getItem(upc, itemID)?.damaged = damaged
            scannedItems.getItem(upc, itemID)?.description = description
            productRecyclerView.adapter?.notifyDataSetChanged()
        }
    }
}