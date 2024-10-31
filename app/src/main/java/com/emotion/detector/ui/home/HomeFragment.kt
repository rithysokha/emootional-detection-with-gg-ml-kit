package com.emotion.detector.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.emotion.detector.databinding.FragmentHomeBinding
import com.emotion.detector.model.State
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel by viewModels<HomeViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
    }

    private fun displayGreeting(greeting: String) {
        binding.secureText.text = greeting
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}