package com.example.practicapig.FireBase

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.getParcelableCompat
import com.example.practicapig.R
import com.example.practicapig.databinding.ActivityFirebaseBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

private lateinit var binding: ActivityFirebaseBinding


class FireBaseActivity: AppCompatActivity() {
    private var usuario: Usuario? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val credentialManager by lazy {
            CredentialManager.create(this)
        }


        binding = ActivityFirebaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //-------------------------------------toolbar---------------------------
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        //recoger intent
        usuario = intent.getParcelableCompat("usuario")
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario no encontrado"

        //cargo el avatar del usuario
        usuario?.avatar?.let {
            Picasso.get().load(it).into(binding.avatarToolbar)
        }

        //boton sin autenticacion
        binding.botonSin.setOnClickListener {
            val intent = Intent(this, FireBaseSin::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)

        }

        //autenticacion google
        auth = FirebaseAuth.getInstance()

        //---------------------Toto este bloque ha salido de la web de firebase, no he cambiado nada-------------------
        // Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(getString(R.string.default_web_client_id))//cambio antes habia:google_app_id
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)//cambio antes true
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        //--------------------------------si vuelvo a usar, copiar tal cual---------------------------------


        //boton con autenticacion
        binding.botonCon.setOnClickListener {
            binding.textEstadoAuth.visibility = View.VISIBLE
            binding.textEstadoAuth.text = "Espere unos segundos..."
            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@FireBaseActivity
                    )

                    handleSignIn(result.credential)

                    // identifico el fallo en particular porque no se que falla
                } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                    binding.textEstadoAuth.text = "Error: ${e.errorMessage}"
                    Log.e("AUTH", "Tipo de error: ${e.type}", e)
                } catch (e: Exception) {
                    binding.textEstadoAuth.text = "Error desconocido"
                    Log.e("AUTH", "Excepción", e)
                }



            }
        }
    }

//------------------------------------------------sacado tal cual de la web de firebase, usar tal cual--------------------------
    private fun handleSignIn(credential: Credential) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential =getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //-------------------------------añadido por mi para pasar a la siguiente pantalla la toolbar---------
                    binding.textEstadoAuth.text = "Autenticación correcta"
                    val intent = Intent(this, FireBaseCon::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    finish()
                    //------------------------------------hasta aqui-----------------------------------------------
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    //--------------------------añadido por mi para mostrar por pantallla el fallo---------------
                    binding.textEstadoAuth.text = "Error al autenticar con Google"
                    //----------------------------------------------------------------------------------------
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    //--------------------------------------------------------------hasta aqui-------------------------------------------
//-----------------------------------a partir de aqui del profe, tambien usar tal cual-------------------------
    private fun updateUI(user: FirebaseUser?) {
    }

}
