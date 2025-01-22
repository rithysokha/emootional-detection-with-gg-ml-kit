package com.emotion.detector.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.emotion.detector.R
import com.emotion.detector.databinding.FragmentProfileBinding
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.emotion.detector.model.Profile
import com.emotion.detector.model.State
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel by viewModels<ProfileViewModel>()
    private var _binding: FragmentProfileBinding? = null
    var profile: Profile? = null
    private val binding get() = _binding!!

    private lateinit var darkModeSpinner: Spinner
    private lateinit var languageSpinner: Spinner
    private lateinit var preferences: SharedPreferences

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
        // Observe to ViewModel state
        viewModel.profileState.observe(viewLifecycleOwner) { profileState ->
            Log.d("ruppite", "[ProfileFragmentKotlin] State")
            when (profileState.state) {
                State.loading -> {
                    Log.d("ruppite", "[ProfileFragmentKotlin] Loading")
                }

                State.success -> {
                    Log.d("ruppite", "[ProfileFragmentKotlin] Success")
                    displayProfile(profileState.data!!)
                }

                State.error -> {
                    Log.d("ruppite", "[ProfileFragmentKotlin] Error")
                }
            }
        }
        // Forward event to ViewModel
        if (profile != null) {
            displayProfile(profile!!)
        } else {
            viewModel.loadProfile()
        }

        // Initialize views
        darkModeSpinner = view.findViewById(R.id.spinner_dark_mode)
        languageSpinner = view.findViewById(R.id.spinner_language)

        // Initialize SharedPreferences
        preferences = requireContext().getSharedPreferences("settings", 0)

        // Set current selection from preferences
        darkModeSpinner.setSelection(preferences.getInt("dark_mode", 0))
        languageSpinner.setSelection(preferences.getInt("language", 0))

        // Setup listeners for spinners
        darkModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Save preference and apply dark mode
                preferences.edit().putInt("dark_mode", position).apply()
                applyDarkMode(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Save preference and update language
                preferences.edit().putInt("language", position).apply()
                applyLanguage(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun displayProfile(profile: Profile) {
        binding.usernameData.text = profile.name
        binding.emailData.text = profile.email
        binding.phoneData.text = profile.phone
        binding.dobData.text = profile.dob
        binding.addressData.text = profile.address
        Picasso.get()
            .load(profile.imgUrl)
            .into(binding.imgProfile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyDarkMode(mode: Int) {
        when (mode) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun applyLanguage(language: Int) {
        val locale = when (language) {
            0 -> "en"
            1 -> "km"
            else -> "en"
        }

        val currentLocale = requireContext().resources.configuration.locales.get(0).language
        if (currentLocale != locale) {
            val localeToSet = java.util.Locale(locale)
            java.util.Locale.setDefault(localeToSet)

            val configuration = requireContext().resources.configuration
            configuration.setLocale(localeToSet)
            configuration.setLayoutDirection(localeToSet)

            val context = requireContext().createConfigurationContext(configuration)
            requireContext().resources.updateConfiguration(
                configuration,
                context.resources.displayMetrics
            )

            // Recreate the activity to apply the new configuration
            activity?.recreate()
        }
    }
}