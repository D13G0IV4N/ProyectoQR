package com.eric.proyectoqr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eric.proyectoqr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanButton.setOnClickListener {
            // Ir a la actividad de escaneo
            startActivity(Intent(this, ScanQrActivity::class.java))
        }
    }
}
