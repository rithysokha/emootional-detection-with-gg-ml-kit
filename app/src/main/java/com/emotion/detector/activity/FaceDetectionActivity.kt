package com.emotion.detector.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.emotion.detector.databinding.ActivityFaceDetectionBinding
import com.emotion.detector.ui.CameraXViewModel
import com.emotion.detector.ui.home.HomeFragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import com.google.mlkit.vision.face.Face

class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceDetectionBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var tfliteInterpreter: Interpreter

    private val cameraXViewModel = viewModels<CameraXViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTFLiteModel()  // Initialize TFLite Model

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        cameraXViewModel.value.processCameraProvider.observe(this) { provider ->
            processCameraProvider = provider
            bindCameraPreview()
            bindInputAnalyser()
        }
    }

    private fun bindCameraPreview() {
        cameraPreview = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
        try {
            processCameraProvider.bindToLifecycle(this, cameraSelector, cameraPreview)
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun initTFLiteModel() {
        val assetFileDescriptor = assets.openFd("0.399_MobileNetV2.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val byteBuffer = inputStream.channel.map(
            java.nio.channels.FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
        tfliteInterpreter = Interpreter(byteBuffer)
    }

    private fun bindInputAnalyser() {
        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .build()
        )
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(detector, imageProxy)
        }

        try {
            processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(detector: FaceDetector, imageProxy: ImageProxy) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        detector.process(inputImage).addOnSuccessListener { faces ->
            binding.graphicOverlay.clear()
            faces.forEach { face ->
                val faceBox = FaceBox(binding.graphicOverlay, face, imageProxy.image!!.cropRect,"hello")
                binding.graphicOverlay.add(faceBox)

                // Preprocess the detected face and predict using TFLite
                val inputBuffer = preprocessFace(face, imageProxy)
                val outputBuffer =
                    ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder()) // Adjusted size

                tfliteInterpreter.run(inputBuffer, outputBuffer)
                outputBuffer.rewind()
                val prediction1 = outputBuffer.float
                val prediction2 = outputBuffer.float

                Log.d(TAG, "Prediction: $prediction1, $prediction2")
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }.addOnCompleteListener {
            imageProxy.close()
        }
    }

    private fun preprocessFace(face: Face, imageProxy: ImageProxy): ByteBuffer {
        // Assuming the model expects 224x224 input size and RGB channels
        val inputSize = 224
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
            .order(ByteOrder.nativeOrder())

        // Extract image data for the detected face
        // You need to implement the actual cropping and resizing logic here
        // Assume cropFaceBitmap returns a resized Bitmap for the face bounding box
        val faceBitmap: Bitmap = cropFaceBitmap(face, imageProxy, inputSize)

        val intValues = IntArray(inputSize * inputSize)
        faceBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)
        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }

    private fun cropFaceBitmap(face: Face, imageProxy: ImageProxy, inputSize: Int): Bitmap {
        // Implement face cropping and resizing to inputSize x inputSize Bitmap
        // Placeholder function: actual implementation depends on your app's requirements
        return Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888)
    }

    companion object {
        private val TAG = FaceDetectionActivity::class.simpleName

        fun startActivity(fragment: HomeFragment) {
            val context = fragment.requireContext() // Get the Context from the Fragment
            Intent(context, FaceDetectionActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
