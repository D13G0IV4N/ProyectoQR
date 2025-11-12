package com.eric.proyectoqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eric.proyectoqr.databinding.ActivityTicketInfoBinding

class TicketInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtiene el dato del QR desde el Intent
        val qrData = intent.getStringExtra("qr_data") ?: "Sin datos"
        binding.qrDataTextView.text = qrData
    }
}
