package com.emotion.detector.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.emotion.detector.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var darkModeSpinner: Spinner
    private lateinit var languageSpinner: Spinner
    private lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
//                applyLanguage(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyDarkMode(mode: Int) {
        when (mode) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

//    private fun applyLanguage(language: Int) {
//        val locale = when (language) {
//            0 -> "en" // English
//            1 -> "km" // Khmer
//            else -> "en"
//        }
//
//        val localeToSet = java.util.Locale(locale)
//        java.util.Locale.setDefault(localeToSet)
//
//        val configuration = requireContext().resources.configuration
//        configuration.setLocale(localeToSet)
//        configuration.setLayoutDirection(localeToSet)
//
//        requireContext().createConfigurationContext(configuration)
//
//        // Optionally restart the activity to apply changes
//        requireActivity().runOnUiThread {
//            requireActivity().recreate()
//        }
//    }
}