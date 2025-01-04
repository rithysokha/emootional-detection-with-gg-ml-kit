package com.emotion.detector.activity

import android.annotation.SuppressLint
import android.content.Context
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
import org.tensorflow.lite.support.common.FileUtil
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
    private var interpreter: Interpreter? = null

    private val cameraXViewModel = viewModels<CameraXViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createInterpreter()  // Initialize TFLite Model

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        cameraXViewModel.value.processCameraProvider.observe(this){
                provider ->
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

    private fun createInterpreter() {
        val tfLiteOptions = Interpreter.Options()
        interpreter = Interpreter(FileUtil.loadMappedFile(this, TFLITE_MODEL_NAME), tfLiteOptions)
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
                val byteBuffer = preprocessFace(face, imageProxy) // Preprocess the face
                val outputBuffer = ByteBuffer.allocateDirect(4 * OUTPUT_CLASSES).order(ByteOrder.nativeOrder())

                // Run inference
                interpreter!!.run(byteBuffer, outputBuffer)

                // Extract predictions
                outputBuffer.rewind()
                val predictions = FloatArray(OUTPUT_CLASSES)
                outputBuffer.asFloatBuffer().get(predictions)

                val predictedEmotion = predictions.indices.maxByOrNull { predictions[it] }
                val emotionLabel = emotionLabels[predictedEmotion ?: 0]
//                Log.d(TAG, "Predicted point: $predictedEmotion")
//                Log.d(TAG, "Predicted Emotion: $emotionLabel")

                // Overlay emotion label
                val faceBox = FaceBox(binding.graphicOverlay, face, imageProxy.image!!.cropRect,
                    predictedEmotion.toString()
                )
                // Update overlay with the emotion
                binding.graphicOverlay.add(faceBox)
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }.addOnCompleteListener {
            imageProxy.close()
        }
    }

    private fun preprocessFace(face: Face, imageProxy: ImageProxy): ByteBuffer {
        val inputSize = 48 // Set the input size to 48x48 for your model
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize) // For float32
            .order(ByteOrder.nativeOrder())

        // Crop and resize face
        val faceBitmap = cropFaceBitmap(face, imageProxy, inputSize)

        // Convert to grayscale and normalize
        val intValues = IntArray(inputSize * inputSize)
        faceBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            // Convert pixel to grayscale value
            val gray = (pixel shr 16 and 0xFF + pixel shr 8 and 0xFF + pixel and 0xFF) / 3.0f / 255.0f
            byteBuffer.putFloat(gray)
        }

        return byteBuffer
    }

    private fun cropFaceBitmap(face: Face, imageProxy: ImageProxy, inputSize: Int): Bitmap {
        // Implement face cropping and resizing to inputSize x inputSize Bitmap
        // Placeholder function: actual implementation depends on your app's requirements
        return Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888)
    }

    private fun getInterpreter(
        context: Context,
        modelName: String,
        tfLiteOptions: Interpreter.Options
    ): Interpreter {
        return Interpreter(FileUtil.loadMappedFile(context, modelName), tfLiteOptions)
    }

    companion object {
        private val TAG = FaceDetectionActivity::class.simpleName
        const val TFLITE_MODEL_NAME = "model.tflite"
        const val OUTPUT_CLASSES = 7
        val emotionLabels = arrayOf("Angry", "Disgusted", "Fear", "Happy", "Sad", "Surprise", "Neutral")

        fun startActivity(fragment: HomeFragment) {
            val context = fragment.requireContext() // Get the Context from the Fragment
            Intent(context, FaceDetectionActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
