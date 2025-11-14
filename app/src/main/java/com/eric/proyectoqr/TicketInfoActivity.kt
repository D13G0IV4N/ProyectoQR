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

        val qr            = intent.getStringExtra("qr_data") ?: "-"
        val status        = intent.getStringExtra("status") ?: "-"
        val message       = intent.getStringExtra("message") ?: "-"
        val eventName     = intent.getStringExtra("eventName") ?: "-"
        val date          = intent.getStringExtra("date") ?: "-"
        val shift         = intent.getStringExtra("shift") ?: "-"
        val mesa          = intent.getStringExtra("mesa") ?: intent.getStringExtra("table") ?: "-"
        val ticketId      = intent.getStringExtra("ticketId") ?: "-"
        val reservationId = intent.getStringExtra("reservationId") ?: "-"
        val usedAt        = intent.getStringExtra("usedAt") ?: "-"

        binding.qrDataTextView.text = "QR: $qr"
        binding.statusChip.text = status.uppercase()

        binding.tvEventName.text = "Evento: $eventName"
        binding.tvDate.text = "Fecha: $date"
        binding.tvShift.text = "Turno: $shift"
        binding.tvMesa.text = "Mesa: $mesa"
        binding.tvTicketId.text = "Ticket ID: $ticketId"
        binding.tvReservationId.text = "Reservación ID: $reservationId"
        binding.tvUsedAt.text = "Usado en: $usedAt"
        binding.tvMessage.text = "Mensaje: $message"

        binding.btnClose.setOnClickListener { finish() }
    }
}
