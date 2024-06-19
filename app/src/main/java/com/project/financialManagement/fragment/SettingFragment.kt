package com.project.financialManagement.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.databinding.FragmentSettingBinding
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.model.CoinModel
import com.project.financialManagement.model.LanguageModel
import java.util.Locale


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        Toast.makeText(requireActivity(), "onViewCreated", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Ensure UI is updated when fragment is resumed
        setupUI()
        Toast.makeText(requireActivity(), "onResume", Toast.LENGTH_SHORT).show()
    }

    private fun setupUI() {
        val sh = SharedPreferencesHelper(requireContext())

        // Coin part
        val listCoin = CoinModel.values().map { it.value } as ArrayList<String>

        var coinCode = CoinModel.values().first { coin -> coin.id == sh.getCoinId() }
        binding.currency.text = coinCode.toString()

        val coinMenu = DropdownMenu(requireContext(), listCoin) { _, position ->
            sh.saveCoinId(position)
            coinCode = CoinModel.values().first { coin -> coin.id == position }
            binding.currency.text = coinCode.toString()
        }

        binding.layoutCurrency.setOnClickListener {
            coinMenu.show()
        }

        // Language part
        val listLang = LanguageModel.values().map { it.value } as ArrayList<String>
        var langCode =
            LanguageModel.values().first { lang -> lang.position == sh.getLangPosition() }
        binding.language.text = langCode.code
        val languageMenu = DropdownMenu(requireContext(), listLang) { _, position ->
            langCode = LanguageModel.values().first { lang -> lang.position == position }
            binding.language.text = langCode.code
            sh.saveLangPosition(position)
            setLocale(requireActivity(), langCode.location)
            recreateActivity()
//            refreshFragment()
            updateUIAfterLanguageChange()
        }

        binding.layoutLanguage.setOnClickListener {
            languageMenu.show()
        }
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)
        println("setting info: Locale set to: $languageCode")
    }

    private fun recreateActivity() {
        activity?.recreate()
        println("setting info: Activity recreated")
    }

//    private fun refreshFragment() {
//        println("setting info: Fragment refreshed")
//        parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
//    }

    private fun updateUIAfterLanguageChange() {
        // Update any UI elements that should reflect the new language
        // For example:
        // binding.currency.text = updatedValue
        // binding.language.text = updatedValue
        // ...
        // Make sure to invalidate the views to apply changes
        binding.root.invalidate()
        Toast.makeText(requireActivity(), "updateUIAfterLanguageChange", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}