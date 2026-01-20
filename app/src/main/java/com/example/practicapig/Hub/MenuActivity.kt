package com.example.practicapig.Hub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Chuckstes.ChuckActivity
import com.example.practicapig.FireBase.FireBaseActivity
import com.example.practicapig.JuegoPig.PrimeraActivity
import com.example.practicapig.R
import com.example.practicapig.databinding.ActivityMenuBinding
import com.squareup.picasso.Picasso


private lateinit var binding: ActivityMenuBinding

class MenuActivity: AppCompatActivity() {
    private var usuario: Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //----------------------------toolbar-------------------------------------
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        //--------------INTENT-----------------------------------
        //recoger intent
        usuario = intent.getParcelableCompat("usuario")
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario no encontrado"

        //cargo el avatar del usuario
        usuario?.avatar?.let {
            Picasso.get().load(it).into(binding.avatarToolbar)
        }


        //-------------------------------------------INTENT---------------------------------------
        //si pulsan la imagen del juego, se va al juego, INTENT vacio
        binding.imagenJuego.setOnClickListener {
            //intent para dirigir al juego, no paso usuario porque no es necesario
            val intent = Intent(this, PrimeraActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)

        }

        //-----------------------------------------INTENT-------------------------------------------
        //si pulso la imagen de chuck les llevo a la pagina de los chistes
        binding.imageChuck.setOnClickListener {
            val intent = Intent(this, ChuckActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT-------------------------------------------
        //si pulso la imagen de firebase, se va a la pagina de firebase
        binding.imageFire.setOnClickListener {
            val intent = Intent(this, FireBaseActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }
    }

    //------------------------------------para el menu del toolbar, es como el profe, hacer tal cual--------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //modifico esto para el intent a la pagina de cambiar contraseña
            R.id.menu_cambiar_contrasenia -> {
                val intent = Intent(this, CContraseniaActivity::class.java)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
//--------------------------------hasta aqui----------------------------
}
inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableExtra(key, T::class.java)
    else
        @Suppress("DEPRECATION") getParcelableExtra(key)