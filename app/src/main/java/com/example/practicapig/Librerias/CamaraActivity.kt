package com.example.practicapig.Librerias

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions

import android.util.Log
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.getParcelableCompat
import com.example.practicapig.databinding.ActivityCamaraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CamaraActivity : AppCompatActivity() {

    private lateinit var bindingCamara: ActivityCamaraBinding
    private var capturadorImagen: ImageCapture? = null
    private var capturadorVideo: VideoCapture<Recorder>? = null
    private var grabacionActual: Recording? = null
    private lateinit var ejecutorCamara: ExecutorService
    private var usuarioActual: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingCamara = ActivityCamaraBinding.inflate(layoutInflater)
        setContentView(bindingCamara.root)

        //--------------------usuario del hub----------------------------------

        usuarioActual = intent.getParcelableCompat("usuario")


        //------------------------cogido de manual camarax---------------------------------
        // Request camera permissions
        if (allPermissionsGranted()) {
            iniciarCamara()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        bindingCamara.imageCaptureButton.setOnClickListener { hacerFoto() }
        bindingCamara.videoCaptureButton.setOnClickListener { hacerVideo() }

        ejecutorCamara = Executors.newSingleThreadExecutor()

    }
    //----------------------hasta aqui,  lo comentado lo que hace es guardar las fotos y video en la galeria del movil-----
    //------------------esta cogido del manual--------------------------------

//    private fun takePhoto() {
//        // Get a stable reference of the modifiable image capture use case
//        val imageCapture = imageCapture ?: return
//
//        // Create time stamped name and MediaStore entry.
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//
//        // Create output options object which contains file + metadata
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//            .build()
//
//        // Set up image capture listener, which is triggered after photo has
//        // been taken
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                override fun
//                        onImageSaved(output: ImageCapture.OutputFileResults){
//                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, msg)
//                }
//            }
//        )
//    }


    private fun hacerFoto() {
        val captura = capturadorImagen ?: return

        // se crea la ruta de la foto
        val archivoFoto = crearFicheroImagen()

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(archivoFoto) //construye el archivo foto con el nombre:"IMG_$timeStamp.jpg"
            .build()

        captura.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(this@CamaraActivity, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@CamaraActivity, "Foto guardada en:\n${archivoFoto.absolutePath}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

//-------------------------cogido del manual, guarda el video en la galeria del movil-----------------------------------
//    // Implements VideoCapture use case, including start and stop capturing.
//    private fun captureVideo() {
//        val videoCapture = this.videoCapture ?: return
//
//        viewBinding.videoCaptureButton.isEnabled = false
//
//        val curRecording = recording
//        if (curRecording != null) {
//            // Stop the current recording session.
//            curRecording.stop()
//            recording = null
//            return
//        }
//
//        // create and start a new recording session
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
//            }
//        }
//
//        val mediaStoreOutputOptions = MediaStoreOutputOptions
//            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//            .setContentValues(contentValues)
//            .build()
//        recording = videoCapture.output
//            .prepareRecording(this, mediaStoreOutputOptions)
//            .apply {
//                if (PermissionChecker.checkSelfPermission(this@CamaraActivity,
//                        Manifest.permission.RECORD_AUDIO) ==
//                    PermissionChecker.PERMISSION_GRANTED)
//                {
//                    withAudioEnabled()
//                }
//            }
//            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
//                when(recordEvent) {
//                    is VideoRecordEvent.Start -> {
//                        viewBinding.videoCaptureButton.apply {
//                            text = "Grabar"
//                            isEnabled = true
//                        }
//                    }
//                    is VideoRecordEvent.Finalize -> {
//                        if (!recordEvent.hasError()) {
//                            val msg = "Video capture succeeded: " +
//                                    "${recordEvent.outputResults.outputUri}"
//                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
//                                .show()
//                            Log.d(TAG, msg)
//                        } else {
//                            recording?.close()
//                            recording = null
//                            Log.e(TAG, "Video capture ends with error: " +
//                                    "${recordEvent.error}")
//                        }
//                        viewBinding.videoCaptureButton.apply {
//                            text = "Reanudar"
//                            isEnabled = true
//                        }
//                    }
//                }
//            }
//
//
//    }

    private fun hacerVideo() {
        val capturaVideo = capturadorVideo ?: return

        // si ya esta grabando, se para
        val grabacionEnCurso = grabacionActual
        if (grabacionEnCurso != null) {
            grabacionEnCurso.stop()
            grabacionActual = null
            bindingCamara.videoCaptureButton.text = "Grabar"
            return
        }

        // aqui se genera la ruta del video
        val archivoVideo = crearFicheroVideo() //se guarda con el nombre:"VID_$timeStamp.mp4"

        val outputOptions = FileOutputOptions.Builder(archivoVideo).build()
        //esto es lo que hace que se guarde el video en la ruta que yo le pido y no en la app galeria del movil
        //MediaStoreOutputOptions guarda en la galeria del movil
        //FileOutputOptions guarda en un archivo privado

        grabacionActual = capturaVideo.output
            .prepareRecording(this, outputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        this@CamaraActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { evento ->
                when (evento) {
                    is VideoRecordEvent.Start -> {
                        bindingCamara.videoCaptureButton.text = "Detener"
                    }

                    is VideoRecordEvent.Finalize -> {
                        grabacionActual = null
                        bindingCamara.videoCaptureButton.text = "Grabar"

                        if (!evento.hasError()) {
                            Toast.makeText(this, "Vídeo guardado en:\n${archivoVideo.absolutePath}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error al grabar vídeo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

//----------------viene del manual------------------------------------------------
    private fun iniciarCamara() {
        val proveedorCamaraFuture = ProcessCameraProvider.getInstance(this)

        proveedorCamaraFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val proveedorCamara: ProcessCameraProvider = proveedorCamaraFuture.get()

            // Preview
            val preview = Preview.Builder() //se encarga de mostrar la imagen de la cámara en pantalla
                .build()
                .also {
                    it.setSurfaceProvider(bindingCamara.viewFinder.surfaceProvider)
                }

            val recorder = Recorder.Builder()//se encarga de grabar el vieo
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                    )
                )
                .build()

            capturadorVideo = VideoCapture.withOutput(recorder)
            capturadorImagen = ImageCapture.Builder().build()

            // Select back camera as a default
            val selectorCamara = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                proveedorCamara.unbindAll()

                // Bind use cases to camera
                proveedorCamara.bindToLifecycle(
                    this, selectorCamara, preview, capturadorImagen, capturadorVideo)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        ejecutorCamara.shutdown()
    }

    //--------------------viene del tutorial, siempre igual------------------------------
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS" //se usa en el codigo comentado
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = //lista de permisos que la app necesita
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { //usa permisos especiales para versiones mas antiguas
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }



    private fun crearFicheroImagen(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreUsuario = usuarioActual?.nombre ?: "default"

        // ruta final de fotos:
        // /storage/emulated/0/Android/data/com.example.practicapig/files/Pictures/NOMBRE_USUARIO/
        val carpetaUsuario = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            nombreUsuario
        )

        if (!carpetaUsuario.exists()) {
            carpetaUsuario.mkdirs()
        }

        return File(carpetaUsuario, "IMG_$timeStamp.jpg")
    }

    private fun crearFicheroVideo(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreUsuario = usuarioActual?.nombre ?: "default"

        // ruta de los videos:
        // /storage/emulated/0/Android/data/com.example.practicapig/files/Movies/NOMBRE_USUARIO/
        val carpetaUsuario = File(
            getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            nombreUsuario
        )

        if (!carpetaUsuario.exists()) {
            carpetaUsuario.mkdirs()
        }

        return File(carpetaUsuario, "VID_$timeStamp.mp4")
    }
}
