package com.eric.proyectoqr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enlaza el layout XML (crea res/layout/activity_main.xml)
        setContentView(R.layout.activity_main)
    }
}
