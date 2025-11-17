package com.example.practicapig.Hub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.DatabaseUsuarios
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.databinding.ActivityCcontraseniaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CContraseniaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCcontraseniaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCcontraseniaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibir usuario
        val usuario = intent.getParcelableCompat<Usuario>("usuario")

        if (usuario == null) {
            finish()  // Evita crash si no llega usuario
            return
        }

        // BOTÓN GUARDAR CAMBIOS
        binding.guardarCambios.setOnClickListener {

            val vieja = binding.viejaContrasenia.text.toString()
            val nueva = binding.nuevaContrasenia.text.toString()
            val repetir = binding.repiteContrasenia.text.toString()

            // Ocultar errores
            binding.textView7.visibility = View.GONE
            binding.textView5.visibility = View.GONE
            binding.textView6.visibility = View.GONE
            binding.textErrorVacio.visibility = View.GONE

            lifecycleScope.launch {

                // Validar campos vacíos
                if (vieja.isEmpty() || nueva.isEmpty() || repetir.isEmpty()) {
                    binding.textErrorVacio.visibility = View.VISIBLE
                    return@launch
                }

                // Verificar contraseña antigua
                if (usuario.contraseña != vieja) {
                    binding.textView7.visibility = View.VISIBLE
                    return@launch
                }

                // Validar nueva contraseña
                var contieneNumero = false
                for (c in nueva) if (c.isDigit()) contieneNumero = true

                if (nueva.length !in 4..10 || !contieneNumero) {
                    binding.textView5.visibility = View.VISIBLE
                    return@launch
                }

                // Confirmación de contraseña
                if (nueva != repetir) {
                    binding.textView6.visibility = View.VISIBLE
                    return@launch
                }

                // Actualizar en la BD
                withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@CContraseniaActivity).usuarioDao()
                    dao.actualizarContrasenia(usuario.nombre, nueva)
                }

                // Volver al menú con usuario actualizado
                val intent = Intent(this@CContraseniaActivity, MenuActivity::class.java)
                val usuarioActualizado = Usuario(usuario.nombre, nueva, usuario.fecha_nacimiento)
                intent.putExtra("usuario", usuarioActualizado)

                startActivity(intent)
                finish()
            }
        }
    }
}



