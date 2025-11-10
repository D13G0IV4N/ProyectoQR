package com.eric.proyectoqr

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.eric.proyectoqr.databinding.ActivityLoginBinding
import com.eric.proyectoqr.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultamos progreso y mensaje de error al iniciar
        binding.progressBar.isVisible = false
        binding.errorTextView.isVisible = false

        // Botón de iniciar sesión
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text?.toString()?.trim().orEmpty()
            val password = binding.passwordEditText.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                binding.errorTextView.text = getString(R.string.error_empty_fields)
                binding.errorTextView.isVisible = true
                return@setOnClickListener
            }

            // Petición al backend
            binding.progressBar.isVisible = true
            binding.errorTextView.isVisible = false
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.login(email, password)
                    if (response.isSuccessful) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        binding.errorTextView.text = getString(R.string.error_login_failed)
                        binding.errorTextView.isVisible = true
                    }
                } catch (e: Exception) {
                    binding.errorTextView.text = getString(R.string.error_network)
                    binding.errorTextView.isVisible = true
                } finally {
                    binding.progressBar.isVisible = false
                }
            }
        }

        // “¿Olvidaste tu contraseña?” (por ahora solo muestra un aviso)
        binding.forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Funcionalidad no implementada todavía", Toast.LENGTH_SHORT).show()
        }

        // “Crear cuenta nueva” (por ahora solo muestra un aviso)
        binding.createAccountText.setOnClickListener {
            Toast.makeText(this, "Funcionalidad no implementada todavía", Toast.LENGTH_SHORT).show()
        }
    }
}
