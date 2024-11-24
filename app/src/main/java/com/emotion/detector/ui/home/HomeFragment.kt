package com.emotion.detector.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.emotion.detector.activity.FaceDetectionActivity
import com.emotion.detector.databinding.FragmentHomeBinding
import com.emotion.detector.model.State
import com.emotion.detector.utils.Action
import com.emotion.detector.utils.cameraPermissionRequest
import com.emotion.detector.utils.isPermissionGranted
import com.emotion.detector.utils.openPermissionSetting

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel>()
    private var action = Action.FACE_DETECTION
    private val cameraPermission = android.Manifest.permission.CAMERA

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe to ViewModel state
        viewModel.greetingState.observe(viewLifecycleOwner) { greetingState ->
            when(greetingState.state) {
                State.success -> {
                    displayGreeting(greetingState.data!!)
                }
                State.error -> {
                     binding.secureText.text="Error..."
                }

                State.loading -> binding.secureText.text="Loading..."
            }
        }
        // Forward event to ViewModel
        viewModel.loadGreeting()

        binding.mesh.setOnClickListener {
            this.action = Action.FACE_DETECTION
            requestCameraAndStart()
        }
    }

    private fun requestCameraAndStart() {
        if (requireContext().isPermissionGranted(cameraPermission)) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        when {
            shouldShowRequestPermissionRationale(cameraPermission) -> {
                requireContext().cameraPermissionRequest(
                    positive = { requireContext().openPermissionSetting() }
                )
            }
            else -> {
                requestPermissionLauncher.launch(cameraPermission)
            }
        }
    }

    private fun startCamera() {
        when (action) {
            Action.FACE_DETECTION -> FaceDetectionActivity.startActivity(this)
        }
    }

    private fun displayGreeting(greeting: String) {
        binding.secureText.text = greeting
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}