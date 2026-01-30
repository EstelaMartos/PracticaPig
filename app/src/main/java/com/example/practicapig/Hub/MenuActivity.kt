package com.example.practicapig.Hub

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Chuckstes.ChuckActivity
import com.example.practicapig.FireBase.FireBaseActivity
import com.example.practicapig.JuegoPig.PrimeraActivity
import com.example.practicapig.Librerias.CamaraActivity
import com.example.practicapig.Librerias.galeria.GaleriaActivity
import com.example.practicapig.Librerias.MusicaActivity
import com.example.practicapig.Librerias.Videos.VideoActivity
import com.example.practicapig.R
import com.example.practicapig.databinding.ActivityMenuBinding
import com.squareup.picasso.Picasso


private lateinit var binding: ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private var usuario: Usuario? = null

    // ------------------ MUSICA (SoundPool) ------------------
    private lateinit var reproductorSonido: SoundPool
    private var idSonido: Int = 0 //id que identifica al sonido, como es el unico, es 1
    private var idTransmision: Int = 0 //id de la transmision actual
    private var musicaCargada = false
    private var muteado = false
    // --------------------------------------------------------

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

        // ---------------------- soundpool----------------------
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)//indica que se está reproduciendo música o algún sonido que forma parte de la experiencia multimedia
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) //se indica que el audio es música
            .build()

        reproductorSonido = SoundPool.Builder() //estiona la carga y reproducción de los sonidos
            .setMaxStreams(1) //solo se va a permitir la reproducción de un sonido al mismo tiempo
            .setAudioAttributes(audioAttributes)
            .build()

        idSonido = reproductorSonido.load(this, R.raw.musica_fondo, 1)

        reproductorSonido.setOnLoadCompleteListener { _, _, status ->//listener que se activa cuando el sonido ha sido completamente cargado en SoundPool
            if (status == 0) {
                musicaCargada = true //si todo ha ido bien y la musica se carga bien, se reproduce la música
                reproducirMusica()
            }
        }
        // ----------------------------------------------------------------------

        // -------- boton altavoz --------
        binding.imageAltavoz.setOnClickListener {
            muteado = !muteado


            if (muteado) {
                //si se activa el mute, se muestra la imagen del altavoz muteada y se para la musica
                pararMusica()
                binding.imageAltavoz.setImageResource(R.drawable.altavoz_mute)
            } else {
                // si la musica esta con volumen, se muestra la imagen del altavoz negro y se reproduce la musica
                reproducirMusica()
                binding.imageAltavoz.setImageResource(R.drawable.altavoz_volumen)
            }

            //--------------------------------ANIMACION BOTON MUTE------------------------
            // inicio la animacion si pulsan el altavoz
            val lottieAnimationView = binding.animacionAltavoz
            lottieAnimationView.playAnimation() //inicia la animacion
        }

        // ----------------------------------------------

        //-------------------------------------------INTENT PIG---------------------------------------
        //si pulsan la imagen del juego, se va al juego, INTENT vacio
        binding.imagenJuego.setOnClickListener {
            pararMusica()
            val intent = Intent(this, PrimeraActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT CHUCKSTES-------------------------------------------
        //si pulso la imagen de chuck les llevo a la pagina de los chistes
        binding.imageChuck.setOnClickListener {
            pararMusica()
            val intent = Intent(this, ChuckActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT FIREBASE-------------------------------------------
        //si pulso la imagen de firebase, se va a la pagina de firebase
        binding.imageFire.setOnClickListener {
            pararMusica()
            val intent = Intent(this, FireBaseActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT CAMARA-------------------------------------------
        //si pulso la imagen de firebase, se va a la pagina de la camara
        binding.imageCamara.setOnClickListener {
            pararMusica()
            val intent = Intent(this, CamaraActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT VIDEOS-------------------------------------------
        //si pulso la imagen de firebase, se va a la pagina de los videos
        binding.imageVideos.setOnClickListener {
            pararMusica()
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT MUSICA-------------------------------------------
        //si pulso la imagen de firebase, se va a la pagina de la música
        binding.imageMusica.setOnClickListener {
            pararMusica()
            val intent = Intent(this, MusicaActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        //-----------------------------------------INTENT GALERIA-------------------------------------------
        //si pulso la imagen de firebase, se va a la pagina de la galería
        binding.imageGaleria.setOnClickListener {
            pararMusica()
            val intent = Intent(this, GaleriaActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }


        //---------------------------------------ANIMACIONES------------------------------------------------
        //animacion mando
        val lottieAnimationView = binding.mandoPlay
        lottieAnimationView.playAnimation()

        // animacion robot
        val lottieAnimationViewRobot = binding.Robot
        lottieAnimationViewRobot.playAnimation()

        // variable efecto estrellas
        val lottieAnimationViewFondo = binding.animacionRobot

        //si pulsan al robot, se ejecuta la animacion de las estrellas detras del robot
        binding.animacionRobot.setOnClickListener {
            lottieAnimationViewFondo.playAnimation()
        }

    }

    // ---------------------- FUNCIONES MUSICA ----------------------

    private fun reproducirMusica() {
        if (musicaCargada && !muteado) {
            idTransmision = reproductorSonido.play(
                idSonido,
                1f,
                1f,
                1,
                -1, // loop infinito
                1f
            )
        }
    }

    private fun pararMusica() {
        if (idTransmision != 0) {
            reproductorSonido.stop(idTransmision)
            idTransmision = 0
        }
    }

    // -------------------------------------------------------------

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

    override fun onDestroy() {
        super.onDestroy()
        pararMusica()
        reproductorSonido.release()
    }
    override fun onResume() {
        super.onResume()
        // cuando vuelvo al HUB, la música debe seguir sonando
        reproducirMusica()
    }

    override fun onPause() {
        super.onPause()
        // cuando salgo del HUB, paro la música
        pararMusica()
    }
}

inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableExtra(key, T::class.java)
    else
        @Suppress("DEPRECATION") getParcelableExtra(key)
