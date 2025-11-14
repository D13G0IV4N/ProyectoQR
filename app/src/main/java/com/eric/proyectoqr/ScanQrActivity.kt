package com.eric.proyectoqr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.eric.proyectoqr.databinding.ActivityScanQrBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class ScanQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // Evita abrir la pantalla dos veces
    @Volatile private var handled = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().apply {
                    setAnalyzer(cameraExecutor) { proxy -> analyzeFrame(scanner, proxy) }
                }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                Log.e("ScanQrActivity", "No se pudo iniciar la cámara", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeFrame(
        scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (handled) return@addOnSuccessListener

                // Toma el primer valor no vacío (rawValue o displayValue)
                val value = barcodes
                    .firstOrNull { !it.rawValue.isNullOrBlank() || !it.displayValue.isNullOrBlank() }
                    ?.rawValue ?: barcodes.firstOrNull()?.displayValue

                if (!value.isNullOrBlank()) {
                    handled = true
                    Log.d("ScanQrActivity", "QR leído: $value")

                    // Lanza TicketInfoActivity con la MISMA clave que ya usas: "qr_data"
                    startActivity(Intent(this, TicketInfoActivity::class.java).apply {
                        putExtra("qr_data", value)

                        // Estos extras quedarán en "-" hasta que integres la llamada a la API aquí
                        putExtra("status", "-")
                        putExtra("message", "-")
                        putExtra("eventName", "-")
                        putExtra("date", "-")
                        putExtra("shift", "-")
                        putExtra("mesa", "-")
                        putExtra("ticketId", "-")
                        putExtra("reservationId", "-")
                        putExtra("usedAt", "-")
                    })
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("ScanQrActivity", "Error analizando imagen", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
