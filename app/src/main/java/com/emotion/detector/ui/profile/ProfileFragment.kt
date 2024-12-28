package com.emotion.detector.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.emotion.detector.databinding.FragmentProfileBinding
import com.emotion.detector.model.Profile
import com.emotion.detector.model.State
import com.squareup.picasso.Picasso

class ProfileFragment: Fragment() {

    private val viewModel by viewModels<ProfileViewModel>()
    private var _binding: FragmentProfileBinding? = null
    var profile: Profile? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ruppite", "onViewCreated Created")

        // Observe to ViewModel state
        viewModel.profileState.observe(viewLifecycleOwner) { profileState ->
            Log.d("ruppite", "[ProfileFragmentKotlin] State")
            when (profileState.state) {
                State.loading -> {
//                    showLoading()
                    Log.d("ruppite", "[ProfileFragmentKotlin] Loading")
                }
                State.success -> {
//                    hideLoading()
                    Log.d("ruppite", "[ProfileFragmentKotlin] Success")
                    displayProfile(profileState.data!!)
                }

                State.error -> {
                    Log.d("ruppite", "[ProfileFragmentKotlin] Error")
//                    hideLoading()
//                    showErrorContent()
                }
            }
        }

        // Forward event to ViewModel
        if (profile != null) {
            displayProfile(profile!!)
        } else {
            viewModel.loadProfile()
        }
    }

    private fun displayProfile(profile: Profile) {
//        binding.usernameData.text = profile.name
//        binding.emailData.text = profile.email
//        binding.phoneData.text = profile.phone
//        binding.addressData.text = profile.address
//        Picasso.get()
//            .load(profile.imgUrl)
//            .into(binding.imgProfile)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
    }
}