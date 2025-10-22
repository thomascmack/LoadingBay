package ca.unb.mobiledev.appdevproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class ScannedList : AppCompatActivity() {
    private lateinit var scannedRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scanned_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scannedList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scannedRecyclerView = findViewById(R.id.scannedRecyclerView)

        val scannedItems = MainActivity.getScannedItems()

        scannedRecyclerView.adapter = MyAdapter(scannedItems.getAggregate())
    }
}