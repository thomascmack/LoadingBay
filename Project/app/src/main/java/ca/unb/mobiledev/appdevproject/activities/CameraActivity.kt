package ca.unb.mobiledev.appdevproject.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ca.unb.mobiledev.appdevproject.R
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class CameraActivity : ComponentActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var resultView: TextView
    private lateinit var exec: Executor
    private lateinit var camera: Camera
    private lateinit var scanner : BarcodeScanner
    private val QR_SCAN_REQUEST = 0
    private val UPC_SCAN_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)

        val request = intent.getIntExtra("request", -1)

        var options = BarcodeScannerOptions.Builder().build()

        if(request == QR_SCAN_REQUEST) {
            options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats( Barcode.FORMAT_QR_CODE)
                .build()
        }
        else if(request == UPC_SCAN_REQUEST) {
            options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_UPC_A)
                .build()
        }

        scanner = BarcodeScanning.getClient(options)

        previewView = findViewById(R.id.previewView)
        resultView = findViewById(R.id.resultView)

        exec = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()!!
                bindPreviewAndImageAnalysis(cameraProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreviewAndImageAnalysis(cameraProvider: ProcessCameraProvider) {
        val previewBuilder: Preview.Builder = Preview.Builder()
        val preview: Preview = previewBuilder.build()

        val imageAnalysisBuilder = ImageAnalysis.Builder()

        imageAnalysisBuilder.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

        val imageAnalysis = imageAnalysisBuilder.build()

        imageAnalysis.setAnalyzer(exec) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if(barcodes.isNotEmpty()) {
                            val value = barcodes[0].rawValue?.toLong()
                            val intent = Intent()
                            intent.putExtra("value", value)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        imageProxy.close()
                    }
            }
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.surfaceProvider = previewView.getSurfaceProvider()

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageAnalysis)
            .build()
        camera =
            cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, useCaseGroup)
    }
}