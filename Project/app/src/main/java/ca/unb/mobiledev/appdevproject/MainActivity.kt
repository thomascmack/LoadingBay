package ca.unb.mobiledev.appdevproject

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.appdevproject.ui.theme.AppDevProjectTheme
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val scanButton : Button = findViewById(R.id.scanButton)

        scanButton.setOnClickListener {
            scanQRCode(this)
        }
    }
}

fun scanQRCode(context : Context) {
    //configure options for qrcode scanner
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    //instantiate scanner
    val scanner = GmsBarcodeScanning.getClient(context, options)

    //start scan and handle results
    scanner.startScan()
        .addOnSuccessListener { barcode ->
            val rawValue: String? = barcode.rawValue
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, rawValue, duration)
            toast.show()
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppDevProjectTheme {
        Greeting("Android")
    }
}