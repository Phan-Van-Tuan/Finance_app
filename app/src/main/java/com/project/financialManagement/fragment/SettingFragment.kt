package com.project.financialManagement.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.R
import com.project.financialManagement.databinding.FragmentSettingBinding
import com.project.financialManagement.helper.FormatHelper
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.model.CoinModel
import com.project.financialManagement.model.LanguageModel
import java.util.Locale


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val sharedPreferences = SharedPreferencesHelper(requireContext())

        setupCoinDropdown(sharedPreferences)
        setupLanguageDropdown(sharedPreferences)
        setupNotificationSwitch()
        setupLimit()
    }

    private fun setupCoinDropdown(sharedPreferences: SharedPreferencesHelper) {
        val coinList = CoinModel.values().map { it.value }
        val currentCoin = CoinModel.values().first { it.id == sharedPreferences.getCoinId() }
        binding.currency.text = currentCoin.toString()
        val coinMenu = DropdownMenu(requireContext(), coinList) { _, position ->
            sharedPreferences.saveCoinId(position)
            val selectedCoin = CoinModel.values().first { it.id == position }
            binding.currency.text = selectedCoin.toString()
        }

        binding.layoutCurrency.setOnClickListener {
            coinMenu.show()
        }
    }

    private fun setupLanguageDropdown(sharedPreferences: SharedPreferencesHelper) {
        val currentLanguage =
            LanguageModel.values().first { it.position == sharedPreferences.getLangPosition() }
        setLocale(requireContext(), currentLanguage.code)
        binding.language.text = currentLanguage.code
        val languageMenu = DropdownMenu(
            requireContext(), listOf(
                requireContext().getString(R.string.english),
                requireContext().getString(R.string.vietnamese)
            )
        ) { _, position ->
            val selectedLanguage = LanguageModel.values().first { it.position == position }
            sharedPreferences.saveLangPosition(position)
            setLocale(requireActivity(), selectedLanguage.code)
            recreateActivity()
        }

        binding.layoutLanguage.setOnClickListener {
            languageMenu.show()
        }
    }

    private fun setupNotificationSwitch() {
        binding.swNotify.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestNotificationPermission()
            } else {
                Toast.makeText(requireActivity(), "not checked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestNotificationPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)
    }

    private fun recreateActivity() {
        activity?.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 112
    }

    private fun setupLimit() {
        val sh = SharedPreferencesHelper(requireActivity())
        val limitDefault = sh.getLimit()
        if (limitDefault != null) {
            binding.limit.text = FormatHelper.formatCurrency(limitDefault.toDouble(), requireActivity())
        }
        binding.layoutLimit.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.limit_title)

            val input = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                setHint(R.string.limit_hint)
            }

            // Create a container to add the input and set padding/margin
            val container = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(50, 50, 50, 50)
                }
                addView(input, params)
            }
            builder.setView(container)
            builder.setPositiveButton(R.string.ok) { dialog, _ ->
                val inputText = input.text.toString().trim()
                val value = inputText.toDoubleOrNull()
                if (value != null) {
                        sh.saveLimit(value.toFloat())
                        binding.limit.text = FormatHelper.formatCurrency(value.toDouble(), requireActivity())
                        dialog.dismiss()
                } else {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            builder.show()
        }
    }
}