package com.eric.proyectoqr

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.eric.proyectoqr.databinding.ActivityTicketInfoBinding
import com.eric.proyectoqr.network.RetrofitClient
import com.eric.proyectoqr.network.ScanTicketRequest
import com.eric.proyectoqr.network.ScanTicketResponse
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import android.view.View
import android.animation.Animator
import android.animation.AnimatorListenerAdapter

class TicketInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lo que viene del escáner
        val qrRaw = intent.getStringExtra("qr_raw") ?: "-"
        val token = intent.getStringExtra("token") ?: ""

        // Mostrar el QR crudo
        binding.qrDataTextView.text = "QR: $qrRaw"

        binding.btnClose.setOnClickListener { finish() }

        if (token.isBlank()) {
            binding.tvMessage.text = "Mensaje: Token vacío, vuelve a escanear."
            return
        }

        // Llamar a la API para validar el boleto
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(this@TicketInfoActivity)

                val response = api.validateTicket(
                    ScanTicketRequest(token = token)
                )

                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        bindTicketInfo(qrRaw, data)
                    } else {
                        binding.tvMessage.text = "Mensaje: respuesta vacía del servidor."
                    }
                } else {
                    val code = response.code()
                    binding.tvMessage.text = "Mensaje: error $code al validar el boleto."
                    Log.e(
                        "TicketInfoActivity",
                        "Error API $code: ${response.errorBody()?.string()}"
                    )
                }

            } catch (e: Exception) {
                Log.e("TicketInfoActivity", "Error llamando API", e)
                binding.tvMessage.text =
                    "Mensaje: error de red: ${e.localizedMessage ?: "desconocido"}"
            }
        }
    }

    private fun bindTicketInfo(qrRaw: String, data: ScanTicketResponse) {

        val status        = data.status ?: "-"
        val eventName     = data.eventName ?: "-"
        val date          = data.date ?: "-"
        val shift         = data.shift ?: "-"
        val mesa          = data.idMesa?.toString() ?: "-"
        val ticketId      = data.ticketId?.toString() ?: "-"
        val reservationId = data.reservationId?.toString() ?: "-"
        val usedAt        = data.usedAt ?: "-"
        val message       = data.message ?: "-"

        Log.d("STATUS_DEBUG", "Status recibido: $status") // 👈 Para ver qué llega EXACTO

        binding.qrDataTextView.text = "QR: $qrRaw"
        binding.statusChip.text = if (status.isBlank()) "-" else status.uppercase()

        binding.tvEventName.text = "Evento: $eventName"
        binding.tvShift.text = "Turno: $shift"
        binding.tvMesa.text = "Mesa: $mesa"
        binding.tvMessage.text = "Mensaje: $message"

        // ✔️ LISTA DE ESTADOS VÁLIDOS (ya incluye USED)
        val validStatuses = listOf("ok", "válido", "valido", "valid", "used")

        val isOk = validStatuses.any { it.equals(status, ignoreCase = true) }

        // Cambiar color del chip
        val bgColorRes = if (isOk) R.color.teal_200 else android.R.color.holo_red_dark
        val textColor  = if (isOk) Color.BLACK else Color.WHITE

        val chip = binding.statusChip as Chip
        val bgColor = ContextCompat.getColor(this, bgColorRes)
        chip.chipBackgroundColor = ColorStateList.valueOf(bgColor)
        chip.setTextColor(textColor)

        // 🔥 ANIMACIÓN DE ÉXITO (solo si el boleto es válido) 🔥
        if (isOk) {
            binding.successAnim.visibility = View.VISIBLE
            binding.successAnim.playAnimation()

            binding.successAnim.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.successAnim.visibility = View.GONE
                }
            })
        }
    }
}
